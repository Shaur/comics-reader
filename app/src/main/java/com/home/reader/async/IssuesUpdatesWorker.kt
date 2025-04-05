package com.home.reader.async

import android.content.Context
import androidx.work.CoroutineWorker
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

    override suspend fun doWork(): Result {
        with(applicationContext) {
            val issues = repository.issuesForUpdate()
            for (i in issues.indices) {
                val issue = issues[i]
                notificator.showProgressNotification(issues.size, i)

                val remoteIssue = api.getIssue(issue.externalId!!)
                val currentPage = remoteIssue.currentPage
                if(currentPage != issue.currentPage) {
                    repository.update(issue.copy(currentPage = currentPage))
                }
            }

            notificator.completeNotification()
        }

        return Result.success()
    }
}