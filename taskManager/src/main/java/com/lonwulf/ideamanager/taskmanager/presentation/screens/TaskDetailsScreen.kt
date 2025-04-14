package com.lonwulf.ideamanager.taskmanager.presentation.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.navigation.Destinations
import com.lonwulf.ideamanager.navigation.NavComposable
import com.lonwulf.ideamanager.navigation.TopLevelDestinations
import com.lonwulf.ideamanager.taskmanager.presentation.component.CommonToolBar
import com.lonwulf.ideamanager.taskmanager.presentation.viewmodel.TaskManagerViewModel
import com.lonwulf.ideamanager.taskmanager.ui.BlueLight
import com.lonwulf.ideamanager.taskmanager.ui.BluePrimary
import com.lonwulf.ideamanager.taskmanager.util.SuccessAlertDialog
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


    if (successDialogState) {
        SuccessAlertDialog(
            onDismissRequest = { errorDialogState = false }, onConfirmation = {
                navHostController.navigate(TopLevelDestinations.HomeScreen.route)
            }, successMsg = "Successfully deleted Task"
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
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(
                        task?.title ?: "",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
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
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Date",
                                tint = BluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        task.timeRange?.let {
                            Text(
                                it,
                                fontSize = 16.sp,
                                color = txtColors
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    task.id?.let {
                                        vm.deleteTask(it)
                                        successDialogState = true
                                    }

                                }
                        )
                    }
                }
                item {
                    Text(
                        "Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    task.description?.let {
                        Text(
                            it,
                            fontSize = 14.sp,
                            color = txtColors,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }


                    Text(
                        "Read More",
                        fontSize = 14.sp,
                        color = BluePrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
                item {
                    Text(
                        "Sub Tasks",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
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