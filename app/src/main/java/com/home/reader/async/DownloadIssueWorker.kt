package com.home.reader.async

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.home.reader.api.ApiHandler
import com.home.reader.api.dto.IssueDto
import com.home.reader.api.dto.SeriesDto
import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.entity.Series
import com.home.reader.persistence.repository.IssueRepository
import com.home.reader.persistence.repository.SeriesRepository
import com.home.reader.utils.coversPath
import com.home.reader.utils.toNormalizedName
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class DownloadIssueWorker(
    context: Context,
    private val api: ApiHandler,
    private val seriesRepository: SeriesRepository,
    private val issueRepository: IssueRepository,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    companion object {
        const val DOWNLOAD_WORKER_SERIES_ID = "worker.download.series.id"
        const val DOWNLOAD_WORKER_ISSUE_ID = "worker.download.issue.id"
        const val DOWNLOAD_WORKER_PROGRESS = "worker.download.progress"
    }

    init {
        Files.createDirectories(context.dataDir.toPath())
        Files.createDirectories(context.coversPath())
    }

    override suspend fun doWork(): Result {
        val externalSeriesId = inputData.getLong(DOWNLOAD_WORKER_SERIES_ID, 0L)
        val externalIssueId = inputData.getLong(DOWNLOAD_WORKER_ISSUE_ID, 0L)

        val seriesDto: SeriesDto = api.getSeries(externalSeriesId)
        val issueDto: IssueDto = api.getIssue(externalIssueId)

        val (seriesId, issueId) = resolveIssueId(seriesDto, issueDto)

        with(applicationContext) {
            val issueDir = filesDir.resolve(issueId.toString())
            issueDir.mkdirs()

            val cover = coversPath().resolve("$issueId.jpg")
            Files.copy(
                URL(api.buildImageUrl("/pages/$externalIssueId/0", "ORIGINAL")).openStream(),
                cover,
                StandardCopyOption.REPLACE_EXISTING
            )

            val digits = issueDto.pagesCount.toString().length
            for (i in 0 until issueDto.pagesCount) {
                val filename = String.format("%0${digits}d.jpg", i)
                Files.copy(
                    URL(api.buildImageUrl("/pages/$externalIssueId/$i")).openStream(),
                    issueDir.resolve(filename).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
                setProgress(progressData(externalIssueId, i, issueDto.pagesCount))
            }
        }

        val output = Data.Builder()
            .putLong(DOWNLOAD_WORKER_ISSUE_ID, issueId)
            .putLong(DOWNLOAD_WORKER_SERIES_ID, seriesId)
            .build()

        return Result.success(output)
    }

    private fun progressData(issueId: Long, currentPage: Int, pagesCount: Int): Data {
        val progress = (currentPage + 1f) / pagesCount
        return Data.Builder()
            .putLong(DOWNLOAD_WORKER_ISSUE_ID, issueId)
            .putFloat(DOWNLOAD_WORKER_PROGRESS, progress)
            .build()
    }

    private suspend fun resolveIssueId(seriesDto: SeriesDto, issueDto: IssueDto): Pair<Long, Long> {
        var series = seriesRepository.getByExternalId(seriesDto.id)
        if (series == null) {
            series = Series(
                name = seriesDto.title,
                normalizeName = seriesDto.title.toNormalizedName(),
                externalId = seriesDto.id
            )
            val seriesId = seriesRepository.insert(series)
            series = series.copy(id = seriesId)
        }

        val seriesId = series.id!!
        var issue = issueRepository.findBySeriesIdAndIssue(seriesId, issueDto.number)
        if (issue?.id != null) {
            return seriesId to issue.id!!
        }

        issue = Issue(
            issue = issueDto.number,
            seriesId = seriesId,
            currentPage = issueDto.currentPage,
            pagesCount = issueDto.pagesCount,
            externalId = issueDto.id
        )

        val issueId = issueRepository.insert(issue)

        return seriesId to issueId
    }
}