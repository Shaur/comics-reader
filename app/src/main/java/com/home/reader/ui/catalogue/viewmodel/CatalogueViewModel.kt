package com.home.reader.ui.catalogue.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.home.reader.api.ApiHandler
import com.home.reader.api.dto.IssueDto
import com.home.reader.api.dto.SeriesDto
import com.home.reader.async.DownloadIssueWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CatalogueViewModel(context: Context, private val api: ApiHandler) : ViewModel() {

    var seriesState = mutableStateOf<List<SeriesDto>>(listOf())
    var issuesState = mutableStateOf<List<IssueDto>>(listOf())
    var downloadProgress = mutableStateOf<Double?>(null)

    private val workerManager = WorkManager.getInstance(context)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            seriesState.value = api.getAllSeries()
        }
    }

    fun refreshIssuesState(seriesId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            issuesState.value = api.getIssues(seriesId)
        }
    }

    fun coverRequest(url: String): String = api.buildImageUrl(url)

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
        val progress = it.progress.getDouble(DownloadIssueWorker.DOWNLOAD_WORKER_PROGRESS, 0.0)
        downloadProgress.value = progress
    }

}