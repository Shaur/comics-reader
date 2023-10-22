package com.home.reader

import android.app.Application
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.home.reader.async.ImportComicsWorkerFactory
import com.home.reader.persistence.AppContainer
import com.home.reader.persistence.AppDataContainer

class ReaderApplication : Application(), Configuration.Provider {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        container = AppDataContainer(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val delegatingWorkerFactory = DelegatingWorkerFactory().also {
            it.addFactory(
                ImportComicsWorkerFactory(
                    container.seriesRepository,
                    container.issueRepository
                )
            )
        }

        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(delegatingWorkerFactory)
            .build()
    }
}