package com.home.reader.async

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.home.reader.archive.ArchiveTool
import com.home.reader.archive.ArchiveToolFactory
import com.home.reader.component.dto.ArchiveMeta
import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.entity.Series
import com.home.reader.persistence.repository.IssueRepository
import com.home.reader.persistence.repository.SeriesRepository
import com.home.reader.utils.coversPath
import com.home.reader.utils.getFileName
import com.home.reader.utils.toNormalizedName
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ImportComicsWorker(
    context: Context,
    private val seriesRepository: SeriesRepository,
    private val issueRepository: IssueRepository,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    companion object {
        const val IMPORT_WORKER_RESULT_KEY = "worker.import.result"
        const val IMPORT_WORKER_URI_KEY = "worker.import.uri"
        const val IMPORT_WORKER_SERIES_ID_KEY = "worker.import.series"
    }

    private lateinit var archiveTool: ArchiveTool

    init {
        Files.createDirectories(context.dataDir.toPath())
        Files.createDirectories(context.coversPath())
    }

    override suspend fun doWork(): Result {
        val uriString = inputData.getString(IMPORT_WORKER_URI_KEY)
        val uri = Uri.parse(uriString)

        val (fileName, input) = getFileInfo(uri) ?: return Result.failure()

        archiveTool = ArchiveToolFactory(fileName).create()

        val meta = archiveTool.getMeta(input)
        val (seriesId, issueId) = resolveIssueId(meta)
        importFile(uri, issueId)

        val result = "${meta.seriesName} #${meta.number}"

        val output = Data.Builder()
            .putString(IMPORT_WORKER_RESULT_KEY, result)
            .putLong(IMPORT_WORKER_SERIES_ID_KEY, seriesId)
            .build()

        return Result.success(output)
    }

    private fun importFile(uri: Uri, issueId: Long) {
        with(applicationContext) {
            val issueDir = filesDir.resolve(issueId.toString())
            issueDir.mkdirs()

            val input = applicationContext.contentResolver.openInputStream(uri) ?: return
            archiveTool.extract(input, issueDir)

//            issueDir.listFiles()?.sortedBy { it.nameWithoutExtension }
//                ?.forEachIndexed { index, file ->
//                    file.renameTo(file.parentFile!!.resolve("$index.jpg"))
//                }

            val cover = coversPath().resolve("$issueId.jpg")
            val firstPage = (issueDir.listFiles() ?: arrayOf()).minByOrNull { it.name }
            firstPage?.let {
                Files.copy(it.toPath(), cover, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    private fun getFileInfo(uri: Uri): Pair<String, InputStream>? {
        return with(applicationContext) {
            val fileName = getFileName(uri) ?: return null
            val input = contentResolver.openInputStream(uri) ?: return null

            return@with fileName to input
        }
    }

    private suspend fun resolveIssueId(meta: ArchiveMeta): Pair<Long, Long> {
        return with(meta) {
            var series = seriesRepository.getSeriesByNormalizeName(seriesName.toNormalizedName())
            if (series == null) {
                series = Series(name = seriesName, normalizeName = seriesName.toNormalizedName())
                val seriesId = seriesRepository.insert(series)
                series = series.copy(id = seriesId)
            }

            var issue = issueRepository.findBySeriesIdAndIssue(series.id!!, number)
            if (issue?.id != null) {
                return@with series.id!! to issue.id!!
            }

            issue = Issue(
                issue = number,
                seriesId = series.id!!,
                pagesCount = pagesCount
            )

            val issueId = issueRepository.insert(issue)

            return@with series.id!! to issueId
        }
    }
}