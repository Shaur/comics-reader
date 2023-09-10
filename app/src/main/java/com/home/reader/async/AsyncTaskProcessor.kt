package com.home.reader.async

import androidx.compose.runtime.MutableState
import com.google.gson.Gson
import com.home.reader.api.ApiProcessor
import com.home.reader.persistence.entity.TaskType
import com.home.reader.persistence.repository.TaskRepository
import com.home.reader.ui.common.GlobalState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AsyncTaskProcessor(
    globalState: MutableState<GlobalState>,
    api: ApiProcessor,
    taskRepository: TaskRepository
) {

    private val executor = Executors.newSingleThreadExecutor()

    init {
        executor.submit(CheckRunnable(globalState, api, taskRepository, executor))
    }

    private class CheckRunnable(
        private val globalState: MutableState<GlobalState>,
        private val api: ApiProcessor,
        private val taskRepository: TaskRepository,
        private val executor: ExecutorService
    ) : Runnable {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        override fun run() {
            scope.launch {
                val tasks = taskRepository.getAll()
                for (task in tasks) {
                    if (task.type == TaskType.READING_STATE) {
                        val payload =
                            Gson().fromJson(task.payload, UpdateReadingStateTask::class.java)
                        try {
                            val success = api.updateReadingState(
                                issueId = payload.issueId,
                                page = payload.page,
                                token = globalState.value.token!!
                            )
                            if (success) taskRepository.delete(task)
                            globalState.value = globalState.value.copy(serviceAvailable = success)
                        } catch (_: Exception) {
                            globalState.value = globalState.value.copy(serviceAvailable = false)
                        }
                    }
                }
            }

            executor.submit(IdleRunnable(globalState, api, taskRepository, executor))
        }
    }

    private class IdleRunnable(
        private val globalState: MutableState<GlobalState>,
        private val api: ApiProcessor,
        private val taskRepository: TaskRepository,
        private val executor: ExecutorService
    ) : Runnable {
        override fun run() {
            Thread.sleep(5000)
            executor.submit(CheckRunnable(globalState, api, taskRepository, executor))
        }

    }
}