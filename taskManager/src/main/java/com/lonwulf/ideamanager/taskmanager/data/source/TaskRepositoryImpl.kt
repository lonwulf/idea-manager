package com.lonwulf.ideamanager.taskmanager.data.source

import com.lonwulf.ideamanager.core.database.dao.TaskItemDao
import com.lonwulf.ideamanager.core.domain.mapper.mapDomainToEntity
import com.lonwulf.ideamanager.core.domain.mapper.mapEntityToDomain
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.taskmanager.domain.repository.TaskRepository

class TaskRepositoryImpl(private val dao: TaskItemDao) : TaskRepository {
    override suspend fun insertTask(task: TaskItem): Long {
        return dao.insert(task.mapDomainToEntity())
    }

    override suspend fun deleteTask(id: Int): Int {
        return dao.deleteTask(id)
    }

    override suspend fun getTaskById(id: Int): TaskItem {
        return dao.getTask(id).mapEntityToDomain()
    }

    override suspend fun updateTask(task: TaskItem): Int {
        return dao.updateTask(task.mapDomainToEntity())
    }
}