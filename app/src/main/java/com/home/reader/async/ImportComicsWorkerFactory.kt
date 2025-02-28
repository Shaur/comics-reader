package com.home.reader.async

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.home.reader.api.ApiHandler
import com.home.reader.persistence.repository.IssueRepository
import com.home.reader.persistence.repository.SeriesRepository

class ImportComicsWorkerFactory(
    private val seriesRepository: SeriesRepository,
    private val issueRepository: IssueRepository,
    private val api: ApiHandler
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            ImportComicsWorker::class.java.name -> ImportComicsWorker(
                context = appContext,
                seriesRepository = seriesRepository,
                issueRepository = issueRepository,
                parameters = workerParameters
            )

            DownloadIssueWorker::class.java.name -> DownloadIssueWorker(
                context = appContext,
                seriesRepository = seriesRepository,
                issueRepository = issueRepository,
                api = api,
                parameters = workerParameters
            )

            else -> null
        }
    }
}