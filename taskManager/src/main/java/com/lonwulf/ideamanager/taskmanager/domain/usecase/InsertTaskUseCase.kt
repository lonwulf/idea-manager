package com.lonwulf.ideamanager.taskmanager.domain.usecase

import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.core.util.GenericUseCaseResult
import com.lonwulf.ideamanager.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.java.KoinJavaComponent.inject

class InsertTaskUseCase {
    private val repository: TaskRepository by inject(TaskRepository::class.java)
    operator fun invoke(task: TaskItem): Flow<GenericUseCaseResult<Long?>> = flow {
        val result = repository.insertTask(task)
        result.takeIf { it > 0 }?.let {
            emit(GenericUseCaseResult(it, isSuccessful = true))
        } ?: emit(GenericUseCaseResult(null, isSuccessful = false, "error inserting"))
    }

}