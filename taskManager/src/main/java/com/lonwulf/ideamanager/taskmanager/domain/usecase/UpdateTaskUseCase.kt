package com.lonwulf.ideamanager.taskmanager.domain.usecase

import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.taskmanager.domain.repository.TaskRepository
import org.koin.java.KoinJavaComponent.inject

class UpdateTaskUseCase {
    private val repository: TaskRepository by inject(TaskRepository::class.java)
    suspend operator fun invoke(task: TaskItem) {
        repository.updateTask(task)

    }
}