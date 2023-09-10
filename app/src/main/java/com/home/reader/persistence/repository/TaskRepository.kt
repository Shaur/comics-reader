package com.home.reader.persistence.repository

import com.home.reader.persistence.entity.Task

interface TaskRepository {

    fun getAll(): List<Task>

    suspend fun insert(task: Task)

    suspend fun delete(task: Task)
}