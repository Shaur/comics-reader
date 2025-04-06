package com.home.reader.async

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.home.reader.api.ApiHandler
import com.home.reader.notification.NotificationHelper
import com.home.reader.persistence.repository.IssueRepository

class IssuesUpdatesWorker(
    context: Context,
    parameters: WorkerParameters,
    private val api: ApiHandler,
    private val repository: IssueRepository,
    private val notificator: NotificationHelper
) : CoroutineWorker(context, parameters) {

    companion object {
        const val UPDATED_SERIES_ID = "worker.updater.series.id"
        const val UPDATED_PAGES_COUNT = "worker.updater.pages.count"
        const val UPDATED_COMPLETION = "worker.updater.completion"
    }

    override suspend fun doWork(): Result {
        with(applicationContext) {
            val issues = repository.issuesForUpdate()
            for (i in issues.indices) {
                val issue = issues[i]
                notificator.showProgressNotification(issues.size, i)

                val remoteIssue = api.getIssue(issue.externalId!!)
                val currentPage = remoteIssue.currentPage
                if (currentPage != issue.currentPage) {
                    repository.update(issue.copy(currentPage = currentPage))
                    setProgress(notifyUpdateResultData(issue.seriesId, currentPage, currentPage + 1 == issue.pagesCount))
                }
            }

            notificator.completeNotification()
        }

        return Result.success()
    }

    private fun notifyUpdateResultData(seriesId: Long, pagesCount: Int, completed: Boolean): Data {
        return Data.Builder()
            .putLong(UPDATED_SERIES_ID, seriesId)
            .putInt(UPDATED_PAGES_COUNT, pagesCount)
            .putBoolean(UPDATED_COMPLETION, completed)
            .build()
    }
}