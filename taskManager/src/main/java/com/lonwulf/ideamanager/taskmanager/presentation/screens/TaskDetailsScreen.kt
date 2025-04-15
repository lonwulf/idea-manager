package com.lonwulf.ideamanager.taskmanager.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.core.util.GenericResultState
import com.lonwulf.ideamanager.navigation.Destinations
import com.lonwulf.ideamanager.navigation.NavComposable
import com.lonwulf.ideamanager.navigation.TopLevelDestinations
import com.lonwulf.ideamanager.taskmanager.presentation.component.CommonToolBar
import com.lonwulf.ideamanager.taskmanager.presentation.viewmodel.TaskManagerViewModel
import com.lonwulf.ideamanager.taskmanager.ui.BlueLight
import com.lonwulf.ideamanager.taskmanager.ui.BluePrimary
import com.lonwulf.ideamanager.taskmanager.util.CircularProgressBar
import com.lonwulf.ideamanager.taskmanager.util.ErrorAlertDialog
import com.lonwulf.ideamanager.taskmanager.util.SuccessAlertDialog
import com.lonwulf.ideamanager.taskmanager.util.cancelReminder
import com.lonwulf.ideamanager.taskmanager.util.scheduleReminderWithWorkManager
import org.koin.androidx.compose.navigation.koinNavViewModel
import java.lang.reflect.Type

class TaskDetailsScreenComposable : NavComposable {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        sheetState: SheetState,
        snackbarHostState: SnackbarHostState
    ) {
        TaskDetailsScreen(navHostController = navHostController)
    }
}

enum class SuccessStateVariant {
    UPDATE_TASK, DELETE_TASK
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskDetailsScreen(modifier: Modifier = Modifier, navHostController: NavHostController) {
    val backStackEntry =
        remember { navHostController.getBackStackEntry(TopLevelDestinations.HomeScreen.route) }
    val txtColors = if (!isSystemInDarkTheme()) Color.Black else BluePrimary
    val containerColors = if (!isSystemInDarkTheme()) BlueLight else Color.White
    val taskString = navHostController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("taskItem")
    val typeToken: Type = object : TypeToken<TaskItem>() {}.type
    val task: TaskItem = Gson().fromJson(taskString, typeToken)
    val vm: TaskManagerViewModel = koinNavViewModel(viewModelStoreOwner = backStackEntry)
    var successDialogState by remember { mutableStateOf(false) }
    var errorDialogState by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    var successDialogStateVariant by remember { mutableStateOf("") }
    val updateTaskState by vm.updateTaskStateFlow.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var errMsg by remember { mutableStateOf("") }


    LaunchedEffect(updateTaskState) {
        when (updateTaskState) {
            is GenericResultState.Loading -> {}
            is GenericResultState.Empty -> isLoading = false
            is GenericResultState.Error -> {
                isLoading = false
                errorDialogState = true
                errMsg = (updateTaskState as GenericResultState.Error).msg ?: ""
            }

            is GenericResultState.Success -> {
                isLoading = false
                scheduleReminderWithWorkManager(ctx, task)
                successDialogStateVariant = SuccessStateVariant.UPDATE_TASK.name
                successDialogState = true
            }
        }
    }

    if (successDialogState) {
        val message =
            if (successDialogStateVariant == SuccessStateVariant.DELETE_TASK.name) "Successfully deleted Task" else "Successfully updated Task"
        SuccessAlertDialog(
            onDismissRequest = { errorDialogState = false }, onConfirmation = {
                navHostController.navigate(TopLevelDestinations.HomeScreen.route)
            }, successMsg = message
        )
    }
    if (errorDialogState) {
        ErrorAlertDialog(
            onDismissRequest = { errorDialogState = false }, onConfirmation = {
                errorDialogState = false
                task.id?.let {
                    cancelReminder(ctx, it)
                }
                val updatedTask = task.copy(status = true)
                vm.updateTask(updatedTask)
            }, errMsg = errMsg
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        CommonToolBar(title = Destinations.TaskDetailsScreen.title, onclick = {
            navHostController.navigateUp()
        })
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {
            CircularProgressBar(isDisplayed = isLoading)
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            task.title ?: "",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .clickable {
                                    val stringTask = Gson().toJson(task)
                                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                        "task",
                                        stringTask
                                    )
                                    navHostController.navigate(Destinations.CreateTasksScreen.route)
                                }
                        ) {
                            Text(
                                "Edit",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                Icons.Default.Create,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier
                                    .size(25.dp)
                            )
                        }
                    }

                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Date",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        task.timeRange?.let {
                            Text(
                                it,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    task.id?.let {
                                        cancelReminder(ctx, it)
                                        vm.deleteTask(it)
                                        successDialogStateVariant =
                                            SuccessStateVariant.DELETE_TASK.name
                                        successDialogState = true
                                    }
                                }
                        )
                    }
                }
                item {
                    Text(
                        "Overview",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    task.description?.let {
                        Text(
                            it,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }


                    Text(
                        "Read More",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary.copy(0.5f),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
                item {
                    Text(
                        "Sub Tasks",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                task.takeIf { it.status == false }?.let {
                    item {
                        Spacer(modifier = Modifier.height(50.dp))
                        Button(
                            onClick = {
                                task.id?.let {
                                    cancelReminder(ctx, it)
                                    val updatedTask = task.copy(status = true)
                                    vm.updateTask(updatedTask)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                            )
                        ) {
                            Text(
                                "Complete Task", fontSize = 16.sp, fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondary,
                            )
                        }
                    }
                }
//            items(subtask) { subtask ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 8.dp),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = CardDefaults.cardColors(containerColor = BlueLight),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .size(40.dp)
//                                .clip(RoundedCornerShape(12.dp))
//                                .background(Color(0xFFE1F5FE)),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                subtask.icon,
//                                fontSize = 20.sp
//                            )
//                        }
//                        Spacer(modifier = Modifier.width(12.dp))
//
//                        Column(
//                            modifier = Modifier.weight(1f)
//                        ) {
//                            Text(
//                                subtask.title,
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.Medium
//                            )
//
//                            Text(
//                                subtask.assignee,
//                                fontSize = 14.sp,
//                                color = Color(0xFF90CAF9)
//                            )
//                        }
//                        Box(
//                            modifier = Modifier
//                                .size(32.dp)
//                                .clip(CircleShape)
//                                .background(BlueDark),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                Icons.Default.Check,
//                                contentDescription = "Completed",
//                                tint = Color.White,
//                                modifier = Modifier.size(16.dp)
//                            )
//                        }
//                    }
//                }
//            }
            }
        }
    }
}