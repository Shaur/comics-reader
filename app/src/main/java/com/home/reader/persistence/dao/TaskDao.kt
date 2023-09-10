package com.home.reader.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.home.reader.persistence.entity.Task

@Dao
interface TaskDao {

    @Query("select * from task")
    fun getAll(): List<Task>

    @Insert
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

}