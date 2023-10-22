package com.home.reader.async

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.home.reader.persistence.repository.IssueRepository
import com.home.reader.persistence.repository.SeriesRepository

class ImportComicsWorkerFactory(
    private val seriesRepository: SeriesRepository,
    private val issueRepository: IssueRepository,
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

            else -> null
        }
    }
}