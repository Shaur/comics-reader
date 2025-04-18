package com.home.reader

import android.app.Application
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.home.reader.async.WorkersFactory
import com.home.reader.persistence.AppContainer
import com.home.reader.persistence.AppDataContainer


class ReaderApplication : Application(), Configuration.Provider {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        container = AppDataContainer(this)
    }

    override val workManagerConfiguration: Configuration
        get() {
        val delegatingWorkerFactory = DelegatingWorkerFactory().also {
            it.addFactory(
                WorkersFactory(
                    container.seriesRepository,
                    container.issueRepository,
                    container.api,
                    container.notificator
                )
            )
        }

        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(delegatingWorkerFactory)
            .build()
    }
}