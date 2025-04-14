package com.lonwulf.ideamanager.taskmanager.domain.usecase

import com.lonwulf.ideamanager.taskmanager.domain.repository.TaskRepository
import org.koin.java.KoinJavaComponent.inject

class DeleteTaskUseCase {
    private val repository: TaskRepository by inject(TaskRepository::class.java)

    suspend operator fun invoke(id: Int) {
        repository.deleteTask(id)
    }
}