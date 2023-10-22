package com.home.reader.persistence

import android.content.Context
import com.home.reader.persistence.repository.IssueRepository
import com.home.reader.persistence.repository.SeriesRepository
import com.home.reader.persistence.repository.impl.DefaultIssueRepository
import com.home.reader.persistence.repository.impl.DefaultSeriesRepository

interface AppContainer {
    val seriesRepository: SeriesRepository
    val issueRepository: IssueRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val seriesRepository = DefaultSeriesRepository(AppDatabase.invoke(context).seriesDao())

    override val issueRepository = DefaultIssueRepository(AppDatabase.invoke(context).issueDao())

}