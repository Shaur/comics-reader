package com.home.reader.persistence

import android.content.Context
import com.home.reader.api.ApiHandler
import com.home.reader.notification.NotificationHelper
import com.home.reader.persistence.repository.IssueRepository
import com.home.reader.persistence.repository.SeriesRepository
import com.home.reader.persistence.repository.UserRepository
import com.home.reader.persistence.repository.impl.DefaultIssueRepository
import com.home.reader.persistence.repository.impl.DefaultSeriesRepository
import com.home.reader.persistence.repository.impl.DefaultUserRepository

interface AppContainer {
    val seriesRepository: SeriesRepository
    val issueRepository: IssueRepository
    val userRepository: UserRepository
    val api: ApiHandler
    val notificator: NotificationHelper
}

class AppDataContainer(context: Context) : AppContainer {

    override val seriesRepository = DefaultSeriesRepository(AppDatabase.invoke(context).seriesDao())

    override val issueRepository = DefaultIssueRepository(AppDatabase.invoke(context).issueDao())

    override val userRepository = DefaultUserRepository(AppDatabase.invoke(context).userDao())

    override val api: ApiHandler = ApiHandler(userRepository)

    override val notificator = NotificationHelper(context)

}