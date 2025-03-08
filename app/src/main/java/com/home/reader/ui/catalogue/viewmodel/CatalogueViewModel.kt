package com.home.reader.ui.catalogue.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.home.reader.api.ApiHandler
import com.home.reader.api.dto.IssueDto
import com.home.reader.api.dto.SeriesDto
import com.home.reader.async.DownloadIssueWorker
import com.home.reader.async.SeriesPagingSource
import com.home.reader.persistence.repository.IssueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CatalogueViewModel(
    context: Context,
    private val api: ApiHandler,
    private val issueRepository: IssueRepository
) : ViewModel() {

    val seriesState: Flow<PagingData<SeriesDto>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SeriesPagingSource(api) },
    )
        .flow
        .cachedIn(viewModelScope)

    var issuesState = mutableStateOf<List<IssueDto>>(listOf())
    var downloadProgress = mutableStateOf<Map<Long, Float>>(mapOf())
    var cached = mutableStateOf<Map<Long, Boolean>>(mapOf())

    private val workerManager = WorkManager.getInstance(context)

    fun refreshIssuesState(seriesId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            issuesState.value = api.getIssues(seriesId)

            val ids = issuesState.value.map { it.id }.toSet()
            cached.value = issueRepository.getCached(ids).associate { it.externalId!! to true }
        }
    }

    fun coverRequest(url: String, size: String = "ORIGINAL"): String = api.buildImageUrl(url, size)

    fun download(seriesId: Long, issueId: Long) {
        val data = Data.Builder()
            .putLong(DownloadIssueWorker.DOWNLOAD_WORKER_SERIES_ID, seriesId)
            .putLong(DownloadIssueWorker.DOWNLOAD_WORKER_ISSUE_ID, issueId)
            .build()

        val downloadIssueWorkerRequest = OneTimeWorkRequestBuilder<DownloadIssueWorker>()
            .setInputData(data)
            .build()

        workerManager.getWorkInfoByIdLiveData(downloadIssueWorkerRequest.id)
            .observeForever(downloadIssueObserver)

        workerManager.enqueue(downloadIssueWorkerRequest)
    }

    private val downloadIssueObserver = Observer<WorkInfo> {
        val issueId = it.progress.getLong(DownloadIssueWorker.DOWNLOAD_WORKER_ISSUE_ID, 0)
        val progress = it.progress.getFloat(DownloadIssueWorker.DOWNLOAD_WORKER_PROGRESS, 0f)
        Log.i("Download", "Download progress $issueId $progress%")
        downloadProgress.value += (issueId to progress)

        if (it.state == WorkInfo.State.SUCCEEDED) {
            cached.value += (issueId to true)
        }
    }

}