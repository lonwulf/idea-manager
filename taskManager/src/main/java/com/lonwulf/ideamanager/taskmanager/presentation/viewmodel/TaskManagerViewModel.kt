package com.lonwulf.ideamanager.taskmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.core.util.GenericResultState
import com.lonwulf.ideamanager.taskmanager.domain.usecase.DeleteTaskUseCase
import com.lonwulf.ideamanager.taskmanager.domain.usecase.InsertTaskUseCase
import com.lonwulf.ideamanager.taskmanager.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TaskManagerViewModel(
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val insertTaskUseCase: InsertTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {
    private var _insertTaskStateFlow = MutableStateFlow<GenericResultState<Boolean>>(
        GenericResultState.Loading
    )
    val insertTaskStateFlow
        get() = _insertTaskStateFlow.asStateFlow()
    private var _updateTaskStateFlow = MutableStateFlow<GenericResultState<Boolean>>(
        GenericResultState.Loading
    )
    val updateTaskStateFlow
        get() = _updateTaskStateFlow.asStateFlow()
    private var _deleteTaskStateFlow = MutableStateFlow<GenericResultState<Boolean>>(
        GenericResultState.Loading
    )
    val deleteTaskStateFlow
        get() = _deleteTaskStateFlow.asStateFlow()

    fun insertTask(task: TaskItem) = viewModelScope.launch(Dispatchers.IO) {
        insertTaskUseCase(task).onStart {
            setInsertTasksResult(GenericResultState.Loading)
        }
            .flowOn(Dispatchers.IO)
            .collect { result ->
                result.takeIf { it.isSuccessful }?.let {
                    setInsertTasksResult(GenericResultState.Success(true))
                } ?: setInsertTasksResult(GenericResultState.Error(msg = result.msg))

            }
    }

    fun updateTask(task: TaskItem) = viewModelScope.launch(Dispatchers.IO) {
        updateTaskUseCase(task)
    }

    fun deleteTask(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        deleteTaskUseCase(id)
    }

    private fun setInsertTasksResult(data: GenericResultState<Boolean>) {
        _insertTaskStateFlow.value = data
    }

    private fun setUpdateTasksResult(data: GenericResultState<Boolean>) {
        _updateTaskStateFlow.value = data
    }

    private fun setDeleteTasksResult(data: GenericResultState<Boolean>) {
        _deleteTaskStateFlow.value = data
    }
}