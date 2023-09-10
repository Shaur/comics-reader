package com.home.reader.persistence

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.home.reader.api.ApiProcessor
import com.home.reader.async.AsyncTaskProcessor
import com.home.reader.persistence.repository.CredentialsRepository
import com.home.reader.persistence.repository.TaskRepository
import com.home.reader.persistence.repository.impl.DefaultCredentialsRepository
import com.home.reader.persistence.repository.impl.DefaultTaskRepository
import com.home.reader.ui.common.GlobalState

interface AppContainer {
    val credentialsRepository: CredentialsRepository
    val taskRepository: TaskRepository
    val api: ApiProcessor
    val asycTaskProcessor: AsyncTaskProcessor
    val globalSate: MutableState<GlobalState>
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val credentialsRepository: CredentialsRepository by lazy {
        DefaultCredentialsRepository(AppDatabase.invoke(context).credentialsDao())
    }

    override val taskRepository: TaskRepository by lazy {
        DefaultTaskRepository(AppDatabase.invoke(context).taskDao())
    }

    override val api: ApiProcessor by lazy {
        ApiProcessor("192.168.0.102", 8080, context)
    }

    override val globalSate: MutableState<GlobalState> = mutableStateOf(GlobalState())

    override val asycTaskProcessor: AsyncTaskProcessor by lazy {
        AsyncTaskProcessor(globalSate, api, taskRepository)
    }

}