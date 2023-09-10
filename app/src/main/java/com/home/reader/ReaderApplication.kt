package com.home.reader

import android.app.Application
import com.home.reader.persistence.AppContainer
import com.home.reader.persistence.AppDataContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class ReaderApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        container = AppDataContainer(this)
    }
}