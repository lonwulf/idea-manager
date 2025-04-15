package com.lonwulf.ideamanager.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.core.util.GenericResultState
import com.lonwulf.ideamanager.navigation.NavComposable
import com.lonwulf.ideamanager.navigation.TopLevelDestinations
import com.lonwulf.ideamanager.presentation.component.ShowEmptyData
import com.lonwulf.ideamanager.presentation.component.TaskCard
import com.lonwulf.ideamanager.presentation.viewmodel.SharedViewModel
import com.lonwulf.ideamanager.taskmanager.util.CircularProgressBar

class HomeScreenComposable(private val sharedViewModel: SharedViewModel) : NavComposable {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        sheetState: SheetState,
        snackbarHostState: SnackbarHostState
    ) {
        HomeScreen(navHostController = navHostController, vm = sharedViewModel)
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    vm: SharedViewModel
) {
    var tasks by remember {
        mutableStateOf(listOf<TaskItem>())
    }
    val fetchTaskState by vm.fetchTasksStateFlow.collectAsState()
    LaunchedEffect(Unit) {
        vm.fetchAllTasks()
    }
    var isLoading by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = fetchTaskState) {
        when (fetchTaskState) {
            is GenericResultState.Loading -> isLoading = true
            is GenericResultState.Empty,
            is GenericResultState.Error -> isLoading = false

            is GenericResultState.Success -> {
                isLoading = false
                tasks =
                    (fetchTaskState as GenericResultState.Success<List<TaskItem>>).result
                        ?: emptyList()
            }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CircularProgressBar(isDisplayed = isLoading)
        tasks.takeIf { it.isNotEmpty() }?.let {
            val progress = calculateProgress(tasks)
            val percentage = (progress * 100).toInt()
            val totalBarWidth = 100.dp
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    MaterialTheme.colorScheme.tertiaryContainer
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight()
                    ) {
                        Text(
                            "Today's progress summary",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "${tasks.size} tasks",
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Progress",
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                "$percentage%",
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .width(totalBarWidth)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.2f
                                    )
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(totalBarWidth * progress)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Today's Task",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = {
                    navHostController.navigate(TopLevelDestinations.AllTasksScreen.route)
                }) {
                    Text(
                        "See All",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
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

fun calculateProgress(taskItems: List<TaskItem>): Float {
    if (taskItems.isEmpty()) return 0f
    val completedCount = taskItems.count { it.status == true }
    return completedCount.toFloat() / taskItems.size
}

