package com.home.reader.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.home.reader.persistence.dao.CredentialsDao
import com.home.reader.persistence.dao.TaskDao
import com.home.reader.persistence.entity.Credentials
import com.home.reader.persistence.entity.Task

@Database(
    entities = [Credentials::class, Task::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun credentialsDao(): CredentialsDao

    abstract fun taskDao(): TaskDao

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
        ).build()
    }
}