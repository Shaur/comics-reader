package com.home.reader.ui.series.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.home.reader.async.ImportComicsWorker
import com.home.reader.async.ImportComicsWorker.Companion.IMPORT_WORKER_SERIES_ID_KEY
import com.home.reader.component.dto.SeriesDto
import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.entity.Series
import com.home.reader.persistence.repository.IssueRepository
import com.home.reader.persistence.repository.SeriesRepository
import com.home.reader.utils.coversPath
import com.home.reader.utils.toNormalizedName
import kotlinx.coroutines.launch
import kotlin.io.path.absolutePathString

class SeriesViewModel(
    context: Context,
    private val repository: SeriesRepository,
    private val issueRepository: IssueRepository
) : ViewModel() {

    var state = mutableStateOf(listOf<SeriesDto>())

    private val workerManager = WorkManager.getInstance(context)

    private val coversDir = context.coversPath()

    fun loadIssues(uris: List<Uri>) {
        for (uri in uris) {
            val data = Data.Builder()
                .putString(ImportComicsWorker.IMPORT_WORKER_URI_KEY, uri.toString())
                .build()

            val importComicsWorkerRequest = OneTimeWorkRequestBuilder<ImportComicsWorker>()
                .setInputData(data)
                .build()

            workerManager.getWorkInfoByIdLiveData(importComicsWorkerRequest.id)
                .observeForever(importFileObserver)

            workerManager.enqueue(importComicsWorkerRequest)
        }
    }

    private val importFileObserver = Observer<WorkInfo> {
        val seriesId = it.outputData.getLong(IMPORT_WORKER_SERIES_ID_KEY, -1L)

        if (seriesId != -1L) {
            insertSeries(seriesId)
        }
    }

    private fun insertSeries(seriesId: Long) {
        viewModelScope.launch {
            val (series, issues) = repository.getSeriesById(seriesId)
            val existsSeries = state.value.find { it.id == seriesId }
            state.value = buildList {
                if (existsSeries == null) {
                    add(convert(series, issues))
                    addAll(state.value)
                } else {
                    add(existsSeries.copy(issuesCount = existsSeries.issuesCount + 1))
                    addAll(state.value - existsSeries)
                }
            }
        }
    }

    private fun convert(series: Series, issues: List<Issue>): SeriesDto {
        val sortedIssues = issues.sortedWith(
            compareBy<Issue> { it.issue.length }.then(naturalOrder())
        )
        return SeriesDto(
            id = series.id!!,
            name = series.name,
            issuesCount = issues.count(),
            completedIssues = issues.count { it.isRead() },
            coverPath = coversDir.resolve("${sortedIssues.last().id}.jpg").absolutePathString()
        )
    }

    fun refresh() {
        viewModelScope.launch {
            state.value = repository.getAll()
                .filter { (_, issues) -> issues.isNotEmpty() }
                .map { (series, issues) -> convert(series, issues) }
        }
    }

    fun renameSeries(id: Long, name: String) {
        viewModelScope.launch {
            val series = repository.getSeriesByNormalizeName(name.toNormalizedName())
            if (series == null) {
                repository.updateSeriesName(id, name)
            } else {
                issueRepository.changeSeriesId(id, series.id!!)
                repository.deleteById(id)
            }

            val resolveId = series?.id ?: id
            val resolveSeries = repository.getSeriesById(resolveId)

            val ss = state.value.filter { it.id != id || it.id != series?.id }
            state.value = buildList {
                addAll(ss)
                add(convert(resolveSeries.series, resolveSeries.issues))
            }
        }

    }

}