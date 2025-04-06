package com.home.reader.ui.common.component.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.home.reader.async.IssuesUpdatesWorker
import com.home.reader.async.IssuesUpdatesWorker.Companion.UPDATED_COMPLETION
import com.home.reader.async.IssuesUpdatesWorker.Companion.UPDATED_PAGES_COUNT
import com.home.reader.async.IssuesUpdatesWorker.Companion.UPDATED_SERIES_ID

class UpdateOnResumeViewModel(context: Context) : ViewModel() {

    private val workManager = WorkManager.getInstance(context)

    fun refresh(onUpdate: (seriesId: Long, pagesCount: Int, completed: Boolean) -> Unit) {
        val workRequest = OneTimeWorkRequestBuilder<IssuesUpdatesWorker>()
            .setConstraints(Constraints(NetworkType.CONNECTED))
            .build()

        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observeForever { data ->
                if (data == null) return@observeForever
                onUpdate(
                    data.progress.getLong(UPDATED_SERIES_ID, 0L),
                    data.progress.getInt(UPDATED_PAGES_COUNT, 0),
                    data.progress.getBoolean(UPDATED_COMPLETION, false)
                )
            }

        workManager.enqueue(workRequest)
    }
}