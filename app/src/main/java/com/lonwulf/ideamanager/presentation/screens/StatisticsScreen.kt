package com.lonwulf.ideamanager.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lonwulf.ideamanager.core.domain.model.DayStatistic
import com.lonwulf.ideamanager.core.domain.model.MonthData
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.core.util.GenericResultState
import com.lonwulf.ideamanager.navigation.NavComposable
import com.lonwulf.ideamanager.presentation.viewmodel.SharedViewModel
import com.lonwulf.ideamanager.taskmanager.ui.BluePrimary
import com.lonwulf.ideamanager.ui.theme.BlueDark
import com.lonwulf.ideamanager.ui.theme.BlueLight
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class StatisticsScreenComposable(private val sharedViewModel: SharedViewModel) : NavComposable {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        sheetState: SheetState,
        snackbarHostState: SnackbarHostState
    ) {
        StatisticsScreen(navHostController = navHostController, vm = sharedViewModel)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    vm: SharedViewModel
) {
    var allTasks by remember {
        mutableStateOf(listOf(TaskItem()))
    }
    val txtColors = if (!isSystemInDarkTheme()) Color.Black else BluePrimary
    val fetchTaskState by vm.fetchTasksStateFlow.collectAsState()

    LaunchedEffect(key1 = fetchTaskState) {
        when (fetchTaskState) {
            is GenericResultState.Loading -> {}
            is GenericResultState.Empty -> {}
            is GenericResultState.Error -> {}
            is GenericResultState.Success -> {
                allTasks =
                    (fetchTaskState as GenericResultState.Success<List<TaskItem>>).result
                        ?: emptyList()
            }
        }
    }

    val currentWeekDates = remember {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value - 1L)
        (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }
    val months = remember {
        val uniqueMonths = allTasks.map { it.date!!.month }.distinct().sorted()

        // Format month names and mark current month as selected
        val currentMonth = LocalDate.now().month
        uniqueMonths.map { month ->
            MonthData(
                month = month,
                name = month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                isSelected = month == currentMonth
            )
        }
    }
    val selectedMonth = remember {
        months.find { it.isSelected }?.month ?: LocalDate.now().month
    }
    val tasksForSelectedMonth = remember {
        allTasks.filter { it.date!!.month == selectedMonth }
    }

    val dailyStats = remember {
        currentWeekDates.map { date ->
            val day = when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> "M"
                DayOfWeek.TUESDAY -> "T"
                DayOfWeek.WEDNESDAY -> "W"
                DayOfWeek.THURSDAY -> "T"
                DayOfWeek.FRIDAY -> "F"
                DayOfWeek.SATURDAY -> "S"
                DayOfWeek.SUNDAY -> "S"
            }

            // Count tasks for this day
            allTasks.takeIf { it.isNotEmpty() }?.let {
                val tasksForDay =
                    tasksForSelectedMonth.filter { it.date!!.dayOfWeek == date.dayOfWeek }
                DayStatistic(
                    day = day,
                    dayOfWeek = date.dayOfWeek,
                    count = tasksForDay.size,
                    isSelected = date.dayOfWeek == DayOfWeek.FRIDAY
                )
            }

        }
    }

    val totalTaskCount = remember { tasksForSelectedMonth.size }

    val recentActivities = remember {
        allTasks.sortedByDescending { it.date }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            "${allTasks.size} Tasks",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            "Assigned to you this week",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(months) { month ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        month.name,
                        fontSize = 16.sp,
                        fontWeight = if (month.isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (month.isSelected) BlueDark else Color.Black
                    )

                    if (month.isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .width(16.dp)
                                .height(2.dp)
                                .background(BlueDark)
                        )
                    }
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {

                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueLight,
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Week")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select period",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            dailyStats.forEachIndexed { index, stat ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stat?.count.toString(),
                        fontSize = 12.sp,
                        color = txtColors,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    val maxCount = dailyStats.maxOf { it?.count!! }.coerceAtLeast(1)
                    val heightFraction = stat?.count!!.toFloat() / maxCount
                    val maxHeight = 100.dp

                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height(maxHeight * heightFraction)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (stat.isSelected) BlueDark
                                else Color.White
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Day label
                    Text(
                        stat.day,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Latest Activities",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
        )
        LazyColumn(
            modifier = Modifier
                .height(300.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allTasks) { activity ->
                ActivityCard(activity)
            }
        }
    }
}

@Composable
fun ActivityCard(activity: TaskItem) {
    val backgroundColor = if (activity.status == true) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val statusColor = if (activity.status == true) Color(0xFF4CAF50) else Color(0xFFFF9800)
    val statusText = if (activity.status == true) "Completed" else "In Progress"
    val containerColors = if (!isSystemInDarkTheme()) com.lonwulf.ideamanager.taskmanager.ui.BlueLight else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = containerColors),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                activity.icon?.let {
                    Text(
                        it,
                        fontSize = 20.sp
                    )
                }

            }

            Spacer(modifier = Modifier.width(16.dp))

            // Activity details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                activity.title?.let {
                    Text(
                        it,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    statusText,
                    fontSize = 14.sp,
                    color = statusColor
                )
            }
        }
    }
}