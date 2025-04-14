package com.lonwulf.ideamanager.taskmanager.presentation.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.core.util.GenericResultState
import com.lonwulf.ideamanager.navigation.Destinations
import com.lonwulf.ideamanager.navigation.NavComposable
import com.lonwulf.ideamanager.navigation.TopLevelDestinations
import com.lonwulf.ideamanager.taskmanager.R
import com.lonwulf.ideamanager.taskmanager.presentation.component.CommonToolBar
import com.lonwulf.ideamanager.taskmanager.presentation.viewmodel.TaskManagerViewModel
import com.lonwulf.ideamanager.taskmanager.ui.BlueDark
import com.lonwulf.ideamanager.taskmanager.ui.BlueLight
import com.lonwulf.ideamanager.taskmanager.ui.BluePrimary
import com.lonwulf.ideamanager.taskmanager.util.CircularProgressBar
import com.lonwulf.ideamanager.taskmanager.util.ErrorAlertDialog
import com.lonwulf.ideamanager.taskmanager.util.SuccessAlertDialog
import com.lonwulf.ideamanager.taskmanager.util.dpToPx
import com.lonwulf.ideamanager.taskmanager.util.toReminderMillis
import com.lonwulf.ideamanager.taskmanager.worker.ReminderWorker
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class CreateTasksScreenComposable : NavComposable {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        sheetState: SheetState,
        snackbarHostState: SnackbarHostState
    ) {
        CreateTaskScreen(
            navHostController = navHostController, snackbarHostState = snackbarHostState
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    val vm = koinViewModel<TaskManagerViewModel>()
    var taskName by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var endTime by remember { mutableStateOf(LocalTime.of(11, 0)) }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    val categories = listOf("Design", "Development", "Research", "Chores")
    val scope = rememberCoroutineScope()
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""
    var isLoading by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val endTimeInteractionSource = remember { MutableInteractionSource() }
    val startTimeInteractionSource = remember { MutableInteractionSource() }
    val insertTaskState by vm.insertTaskStateFlow.collectAsState()
    var errMsg by remember { mutableStateOf("") }
    var errorDialogState by remember { mutableStateOf(false) }
    var successDialogState by remember { mutableStateOf(false) }
    var task: TaskItem? = null
    val ctx = LocalContext.current


    if (errorDialogState) {
        ErrorAlertDialog(
            onDismissRequest = { errorDialogState = false }, onConfirmation = {
                errorDialogState = false
                task = TaskItem(
                    title = taskName,
                    description = description,
                    category = selectedCategory
                )
                task?.let {
                    vm.insertTask(it)
                }
            }, errMsg = errMsg
        )
    }
    if (successDialogState) {
        SuccessAlertDialog(
            onDismissRequest = { errorDialogState = false }, onConfirmation = {
                navHostController.navigate(TopLevelDestinations.HomeScreen.route)
            }, successMsg = "Successfully created Task"
        )
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    showDatePicker = true
                }
            }
        }
    }
    LaunchedEffect(endTimeInteractionSource) {
        endTimeInteractionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    showEndTimePicker = true
                }
            }
        }
    }
    LaunchedEffect(startTimeInteractionSource) {
        startTimeInteractionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    showStartTimePicker = true
                }
            }
        }
    }
    LaunchedEffect(key1 = insertTaskState) {
        when (insertTaskState) {
            is GenericResultState.Loading -> {}
            is GenericResultState.Empty -> isLoading = false
            is GenericResultState.Error -> {
                isLoading = false
                errorDialogState = true
                errMsg = (insertTaskState as GenericResultState.Error).msg ?: ""
            }

            is GenericResultState.Success -> {
                isLoading = false
                task?.let {
                    scheduleReminderWithWorkManager(ctx, it)
                }
                successDialogState = true
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        CommonToolBar(title = Destinations.CreateTasksScreen.title, onclick = {
            navHostController.navigateUp()
        })
        CircularProgressBar(isDisplayed = isLoading)

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            Text(
                "Task Name",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray, focusedBorderColor = BluePrimary
                ),
                placeholder = { Text("Enter task name") })
            Text(
                "Category",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (isSelected) BlueDark else BlueLight,
                            labelColor = if (isSelected) Color.White else Color.Black
                        )
                    )
                }
            }
            Text(
                "Date & Time",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    interactionSource = interactionSource,
                    value = selectedDate,
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray, focusedBorderColor = BluePrimary
                    ),
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange, contentDescription = "Select date"
                        )
                    })
                errorMessage?.let {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = it, duration = SnackbarDuration.Short
                        )
                    }
                }
                DatePickerDialogComponent(
                    onError = { errorMessage = it },
                    datePickerState = datePickerState,
                    showDatePicker = showDatePicker,
                    onDismiss = { showDatePicker = false })
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Start time
                Column(modifier = Modifier.Companion.weight(1f)) {
                    Text(
                        "Start time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        interactionSource = startTimeInteractionSource,
                        value = startTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = BlueDark
                        ),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.KeyboardArrowDown, contentDescription = "Select time"
                            )
                        })
                }

                // End time
                Column(modifier = Modifier.Companion.weight(1f)) {
                    Text(
                        "End time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        interactionSource = endTimeInteractionSource,
                        value = endTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray, focusedBorderColor = BluePrimary
                        ),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.KeyboardArrowDown, contentDescription = "Select time"
                            )
                        })
                }
            }
            Text(
                "Description",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray, focusedBorderColor = BluePrimary
                ),
                placeholder = { Text("Add task description") })
            Spacer(modifier = Modifier.Companion.weight(1f))
            val required = stringResource(R.string.required)
            Button(
                onClick = {
                    val (isSuccess, msg) = isValidTask(
                        title = taskName,
                        dueDate = endTime.toString(),
                        required = required
                    )
                    if (isSuccess) {
                        isLoading = true
                        task = TaskItem(
                            title = taskName,
                            description = description,
                            category = selectedCategory,
                            timeRange = "$startTime - $endTime"
                        )
                        vm.insertTask(task)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = msg, duration = SnackbarDuration.Long
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary
                )
            ) {
                Text(
                    "Create Task", fontSize = 16.sp, fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showStartTimePicker) {
        ClockPickerDialog(
            initialTime = startTime,
            onTimeSelected = {
                startTime = it
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        ClockPickerDialog(
            initialTime = endTime,
            onTimeSelected = {
                endTime = it
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
private fun scheduleReminderWithWorkManager(context: Context, task: TaskItem) {
    val reminderTimeMillis = task.toReminderMillis() ?: return
    val delayMillis = reminderTimeMillis - System.currentTimeMillis()
    if (delayMillis <= 0) return // Task is in the past, skip

    val inputData = Data.Builder()
        .putString("TASK_TITLE", task.title)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}

fun isValidTask(
    title: String?, dueDate: String?, required: String
): Pair<Boolean, String> {
    return if (title.isNullOrEmpty() || dueDate.isNullOrEmpty()) Pair(
        false, required
    ) else {
        Pair(true, "")
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogComponent(
    showDatePicker: Boolean,
    onDismiss: () -> Unit,
    onError: (String?) -> Unit,
    datePickerState: DatePickerState
) {
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    val selectedLocalDate = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    selectedLocalDate?.let {
                        if (it.isBefore(LocalDate.now())) {
                            onError("You cannot select a past date.")
                        } else {
                            onError(null)
                            onDismiss()
                        }
                    } ?: run {
                        onError("Please select a valid date.")
                    }
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockPickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialTime.hour) }
    var selectedMinute by remember { mutableStateOf(initialTime.minute) }
    var isHourSelection by remember { mutableStateOf(true) }
    var is24HourFormat by remember { mutableStateOf(false) }
    var isAM by remember { mutableStateOf(initialTime.hour < 12) }
    val ctx = LocalContext.current

    val displayHour = remember(selectedHour, is24HourFormat) {
        when {
            is24HourFormat -> selectedHour
            selectedHour == 0 -> 12
            selectedHour > 12 -> selectedHour - 12
            else -> selectedHour
        }
    }

    val hourString = remember(displayHour) {
        displayHour.toString().padStart(2, '0')
    }

    val minuteString = remember(selectedMinute) {
        selectedMinute.toString().padStart(2, '0')
    }

    val period = remember(isAM) {
        if (isAM) "AM" else "PM"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Select Time",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                // Digital time display
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = hourString,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isHourSelection) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.clickable { isHourSelection = true }
                    )

                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Text(
                        text = minuteString,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (!isHourSelection) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.clickable { isHourSelection = false }
                    )

                    if (!is24HourFormat) {
                        Spacer(modifier = Modifier.width(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AM",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isAM) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier
                                    .clickable {
                                        isAM = true
                                        if (!isAM && selectedHour >= 12) {
                                            selectedHour -= 12
                                        } else if (!isAM && selectedHour < 12) {
                                            selectedHour += 12
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )

                            Text(
                                text = "PM",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (!isAM) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier
                                    .clickable {
                                        isAM = false
                                        if (isAM && selectedHour < 12) {
                                            selectedHour += 12
                                        } else if (!isAM && selectedHour >= 12) {
                                            selectedHour -= 12
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Analog clock face
                Box(
                    modifier = Modifier
                        .size(256.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Clock face
                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                        color = Color.White,
                        shape = CircleShape
                    ) {}

                    Surface(
                        modifier = Modifier
                            .size(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {}

                    // Hour numbers
                    if (isHourSelection) {
                        for (hour in 1..12) {
                            val angleRad = (hour * (2 * PI / 12) - PI / 2).toFloat()
                            val radius = 108.dp
                            val x = cos(angleRad) * dpToPx(radius)
                            val y = sin(angleRad) * dpToPx(radius)

                            val isSelected = when {
                                is24HourFormat -> hour == selectedHour || (hour + 12) == selectedHour
                                selectedHour == 0 -> hour == 12
                                selectedHour > 12 -> hour == selectedHour - 12
                                else -> hour == selectedHour
                            }

                            Box(
                                modifier = Modifier
                                    .offset(x = x.dp, y = y.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        selectedHour = when {
                                            is24HourFormat -> hour
                                            !isAM -> if (hour == 12) 12 else hour + 12
                                            else -> if (hour == 12) 0 else hour
                                        }
                                        isHourSelection = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = hour.toString(),
                                    fontSize = 16.sp,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            }
                        }

                        // Clock hand
                        val handAngleRad = when {
                            selectedHour == 0 -> (12 * (2 * PI / 12) - PI / 2).toFloat()
                            selectedHour > 12 -> ((selectedHour - 12) * (2 * PI / 12) - PI / 2).toFloat()
                            else -> (selectedHour * (2 * PI / 12) - PI / 2).toFloat()
                        }

                        val handLength = dpToPx(90.dp)
                        val endX = cos(handAngleRad) * handLength
                        val endY = sin(handAngleRad) * handLength

                        // Draw clock hand
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(2.dp)
                                .rotate(handAngleRad * (180 / PI.toFloat()) + 90)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    } else {
                        // Minute markers
                        for (minute in 0..55 step 5) {
                            val angleRad = (minute * (2 * PI / 60) - PI / 2).toFloat()
                            val radius = dpToPx(108.dp)
                            val x = cos(angleRad) * radius
                            val y = sin(angleRad) * radius

                            val displayMinute = minute
                            val isSelected = displayMinute == selectedMinute

                            Box(
                                modifier = Modifier
                                    .offset(x = x.dp, y = y.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        selectedMinute = displayMinute
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displayMinute.toString().padStart(2, '0'),
                                    fontSize = 16.sp,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            }
                        }

                        // Minute hand
                        val handAngleRad = (selectedMinute * (2 * PI / 60) - PI / 2).toFloat()
                        val handLength = 90.dp

                        // Draw minute hand
                        Box(
                            modifier = Modifier
                                .width(handLength)
                                .height(2.dp)
                                .rotate(handAngleRad * (180 / PI.toFloat()) + 90)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val finalHour = if (!is24HourFormat) {
                                when {
                                    !isAM && selectedHour < 12 -> selectedHour + 12
                                    isAM && selectedHour == 12 -> 0
                                    else -> selectedHour
                                }
                            } else {
                                selectedHour
                            }

                            onTimeSelected(LocalTime.of(finalHour, selectedMinute))
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}