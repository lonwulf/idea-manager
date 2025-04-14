package com.lonwulf.ideamanager.domain.repository

import com.lonwulf.ideamanager.core.domain.model.TaskItem

interface TaskRepository {
    suspend fun getAllTasks():List<TaskItem>
}