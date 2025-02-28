package com.home.reader.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.home.reader.persistence.dao.IssueDao
import com.home.reader.persistence.dao.SeriesDao
import com.home.reader.persistence.dao.UserDao
import com.home.reader.persistence.entity.Issue
import com.home.reader.persistence.entity.Series
import com.home.reader.persistence.entity.User

@Database(
    entities = [Issue::class, Series::class, User::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun seriesDao(): SeriesDao

    abstract fun issueDao(): IssueDao

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "reader-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}