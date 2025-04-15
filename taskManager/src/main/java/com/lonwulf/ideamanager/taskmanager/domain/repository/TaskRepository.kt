package com.lonwulf.ideamanager.taskmanager.domain.repository

import com.lonwulf.ideamanager.core.domain.model.TaskItem

interface TaskRepository {
    suspend fun insertTask(task: TaskItem): Long
    suspend fun deleteTask(id: Int): Int
    suspend fun getTaskById(id: Int): TaskItem
    suspend fun updateTask(task: TaskItem):Int
}