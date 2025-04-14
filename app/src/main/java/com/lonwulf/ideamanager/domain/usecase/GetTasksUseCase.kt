package com.lonwulf.ideamanager.domain.usecase

import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.core.util.GenericUseCaseResult
import com.lonwulf.ideamanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject

class GetTasksUseCase {
    private val repository: TaskRepository by inject(TaskRepository::class.java)
    operator fun invoke(): Flow<GenericUseCaseResult<List<TaskItem>>> = flow {
        val data = repository.getAllTasks()
        data.takeIf { it.isNotEmpty() }?.let {
            emit(GenericUseCaseResult(result = it, isSuccessful = true))
        } ?: emit(GenericUseCaseResult(result = emptyList(), isSuccessful = false, msg = "empty"))

    }
}