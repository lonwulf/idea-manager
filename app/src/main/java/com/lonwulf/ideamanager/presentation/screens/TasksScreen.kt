package com.lonwulf.ideamanager.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lonwulf.ideamanager.core.domain.model.Status
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.core.util.GenericResultState
import com.lonwulf.ideamanager.domain.model.FilterItem
import com.lonwulf.ideamanager.navigation.NavComposable
import com.lonwulf.ideamanager.presentation.component.FilterRowComponent
import com.lonwulf.ideamanager.presentation.component.GenericBottomSheet
import com.lonwulf.ideamanager.presentation.component.ShowEmptyData
import com.lonwulf.ideamanager.presentation.component.TaskCard
import com.lonwulf.ideamanager.presentation.viewmodel.SharedViewModel
import com.lonwulf.ideamanager.taskmanager.util.CircularProgressBar
import kotlinx.coroutines.launch

class TasksScreenComposable(private val sharedViewModel: SharedViewModel) : NavComposable {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        sheetState: SheetState,
        snackbarHostState: SnackbarHostState
    ) {
        TasksScreen(
            navHostController = navHostController,
            vm = sharedViewModel,
            sheetState = sheetState
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    vm: SharedViewModel,
    sheetState: SheetState
) {
    var tasks by remember {
        mutableStateOf(listOf<TaskItem>())
    }
    val statusList by remember { mutableStateOf(Status.entries.map { status -> status.name }) }

    val fetchTaskState by vm.fetchTasksStateFlow.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val rowItems =
        listOf(FilterItem(name = "GroupBy Status"), FilterItem(name = "FilterBy Status"))


    LaunchedEffect(key1 = fetchTaskState) {
        when (fetchTaskState) {
            is GenericResultState.Loading -> {}
            is GenericResultState.Empty -> {}
            is GenericResultState.Error -> {}
            is GenericResultState.Success -> {
                tasks =
                    (fetchTaskState as GenericResultState.Success<List<TaskItem>>).result
                        ?: emptyList()
            }
        }
    }
    if (sheetState.isVisible) {
        GenericBottomSheet(
            sourceStrings = statusList,
            sheetState = sheetState,
            onclick = { name ->
                vm.filterTaskByStatus(name)
            })
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CircularProgressBar(isDisplayed = isLoading)
        FilterRowComponent(modifier = modifier, rowItems) {
            when (it) {
                "GroupBy Status" -> vm.sortTasksByCompletion()
                "FilterBy Status" -> {
                    scope.launch {
                        if (sheetState.isVisible) {
                            sheetState.hide()
                        } else {
                            sheetState.expand()
                        }
                    }
                }

                else -> {}
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        tasks.takeIf { it.isNotEmpty() }?.let {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks) { task ->
                    TaskCard(task, navHostController)
                }
            }
        } ?: ShowEmptyData()
    }
}