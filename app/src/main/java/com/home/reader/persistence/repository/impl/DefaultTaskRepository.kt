package com.home.reader.persistence.repository.impl

import com.home.reader.persistence.dao.TaskDao
import com.home.reader.persistence.entity.Task
import com.home.reader.persistence.repository.TaskRepository

class DefaultTaskRepository(private val dao: TaskDao) : TaskRepository {

    override fun getAll(): List<Task> = dao.getAll()

    override suspend fun insert(task: Task) = dao.insert(task)

    override suspend fun delete(task: Task) = dao.delete(task)
}