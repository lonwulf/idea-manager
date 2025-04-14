package com.lonwulf.ideamanager.presentation.screens

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.google.gson.Gson
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.core.util.GenericResultState
import com.lonwulf.ideamanager.navigation.Destinations
import com.lonwulf.ideamanager.navigation.NavComposable
import com.lonwulf.ideamanager.presentation.viewmodel.SharedViewModel
import com.lonwulf.ideamanager.ui.theme.BlueDark
import com.lonwulf.ideamanager.ui.theme.BlueLight
import com.lonwulf.ideamanager.ui.theme.BluePrimary

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
        mutableStateOf(listOf(TaskItem()))
    }
    val fetchTaskState by vm.fetchTasksStateFlow.collectAsState()
    LaunchedEffect(Unit) {
        vm.fetchAllTasks()
    }

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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        tasks.takeIf { it.isNotEmpty() }?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    BlueLight,
                                    BlueDark
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
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "${tasks.size} tasks",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Progress",
                                color = Color.White,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                "40%",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White)
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
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { /* Handle see all */ }) {
                    Text(
                        "See All",
                        color = Color.Gray,
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

@Composable
fun ShowEmptyData() {

}

@Composable
fun TaskCard(task: TaskItem, navHostController: NavHostController) {
    val txtColors = if (!isSystemInDarkTheme()) Color.Black else BluePrimary
    val containerColors = if (!isSystemInDarkTheme()) BlueLight else Color.White
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val stringTask = Gson().toJson(task)
                navHostController.currentBackStackEntry?.savedStateHandle?.set(
                    "taskItem",
                    stringTask
                )
                navHostController.navigate(Destinations.TaskDetailsScreen.route)
            }
            .height(70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColors,
            contentColor = txtColors
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                task.icon?.let {
                    Text(
                        it,
                        fontSize = 20.sp
                    )
                }

            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                task.title?.let {
                    Text(
                        it,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                task.timeRange?.let {
                    Text(
                        it,
                        fontSize = 12.sp
                    )
                }

            }

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Details",
                tint = Color.Gray
            )
        }
    }
}

