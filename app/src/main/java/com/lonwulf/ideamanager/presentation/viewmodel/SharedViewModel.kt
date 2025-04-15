package com.lonwulf.ideamanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.core.util.GenericResultState
import com.lonwulf.ideamanager.domain.usecase.GetTasksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SharedViewModel(private val useCase: GetTasksUseCase) : ViewModel() {
    private var _fetchTasksStateFlow = MutableStateFlow<GenericResultState<List<TaskItem>>>(
        GenericResultState.Loading
    )
    val fetchTasksStateFlow
        get() = _fetchTasksStateFlow.asStateFlow()
    private var tasks = mutableListOf<TaskItem>()


//    init {
//        fetchAllTasks()
//    }

    fun fetchAllTasks() = viewModelScope.launch(Dispatchers.IO) {
        useCase().onStart {
            setTasksResult(GenericResultState.Loading)
        }
            .flowOn(Dispatchers.IO)
            .collect { result ->
                result.takeIf { it.isSuccessful }?.let {
                    tasks = it.result.toMutableList()
                    setTasksResult(GenericResultState.Success(result = it.result))
                } ?: setTasksResult(GenericResultState.Empty)

            }
    }

    fun sortTasksByCompletion() {
        tasks.takeIf { it.isNotEmpty() }?.sortBy {
            it.status
        }
        setTasksResult(GenericResultState.Success(tasks))
    }

    fun filterTaskByStatus(searchString: String) {
        val finalList =
            tasks.takeIf { it.isNotEmpty() }?.filter { it.getStatusCompletion() == searchString }
                ?: emptyList()
        setTasksResult(GenericResultState.Success(finalList))
    }

    private fun setTasksResult(data: GenericResultState<List<TaskItem>>) {
        _fetchTasksStateFlow.value = data
    }
}