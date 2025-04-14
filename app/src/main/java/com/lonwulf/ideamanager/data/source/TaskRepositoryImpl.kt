package com.lonwulf.ideamanager.data.source

import com.lonwulf.ideamanager.core.database.dao.TaskItemDao
import com.lonwulf.ideamanager.core.domain.mapper.mapEntityListToDomainList
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.domain.repository.TaskRepository

class TaskRepositoryImpl(private val dao: TaskItemDao) : TaskRepository {
    override suspend fun getAllTasks(): List<TaskItem> {
        val tasks = dao.getTasks().mapEntityListToDomainList()
        return tasks
    }
}