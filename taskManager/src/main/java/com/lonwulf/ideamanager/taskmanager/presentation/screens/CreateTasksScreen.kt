package com.lonwulf.ideamanager.taskmanager.presentation.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.lonwulf.ideamanager.taskmanager.R
import com.lonwulf.ideamanager.taskmanager.presentation.component.ClockPickerDialog
import com.lonwulf.ideamanager.taskmanager.presentation.component.CommonToolBar
import com.lonwulf.ideamanager.taskmanager.presentation.component.DatePickerDialogComponent
import com.lonwulf.ideamanager.taskmanager.presentation.viewmodel.TaskManagerViewModel
import com.lonwulf.ideamanager.taskmanager.ui.BlueDark
import com.lonwulf.ideamanager.taskmanager.ui.BluePrimary
import com.lonwulf.ideamanager.taskmanager.util.CircularProgressBar
import com.lonwulf.ideamanager.taskmanager.util.ErrorAlertDialog
import com.lonwulf.ideamanager.taskmanager.util.SuccessAlertDialog
import com.lonwulf.ideamanager.taskmanager.util.scheduleReminderWithWorkManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale

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

enum class UpdateStateVariant {
    UPDATE_TASK, CREATE_TASK
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
    var selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""
    var isLoading by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val endTimeInteractionSource = remember { MutableInteractionSource() }
    val startTimeInteractionSource = remember { MutableInteractionSource() }
    val insertTaskState by vm.insertTaskStateFlow.collectAsState()
    val updateTaskState by vm.updateTaskStateFlow.collectAsState()
    var errMsg by remember { mutableStateOf("") }
    var errorDialogState by remember { mutableStateOf(false) }
    var successDialogState by remember { mutableStateOf(false) }
    var task: TaskItem? = null
    val ctx = LocalContext.current
    var successDialogStateVariant by remember { mutableStateOf("") }


    val taskString = navHostController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("task")
    val typeToken: Type = object : TypeToken<TaskItem>() {}.type


    if (taskString.isNullOrEmpty().not()) {
        task = Gson().fromJson(taskString, typeToken)
        task?.let {
            taskName = it.title ?: ""
            selectedDate = it.date.toString()
        }

    }

    if (errorDialogState) {
        ErrorAlertDialog(
            onDismissRequest = { errorDialogState = false }, onConfirmation = {
                errorDialogState = false
                task = TaskItem(
                    title = taskName,
                    description = description,
                    category = selectedCategory,
                    timeRange = "$startTime - $endTime",
                    date = selectedDate.toLocalDate()
                )
                task?.let {
                    vm.insertTask(it)
                }
            }, errMsg = errMsg
        )
    }
    if (successDialogState) {
        val message =
            if (successDialogStateVariant == UpdateStateVariant.CREATE_TASK.name) "Successfully created Task" else "Successfully updated Task"
        SuccessAlertDialog(
            onDismissRequest = { errorDialogState = false }, onConfirmation = {
                navHostController.navigate(TopLevelDestinations.HomeScreen.route)
            }, successMsg = message
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
    LaunchedEffect(key1 = insertTaskState, key2 = updateTaskState) {
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
                successDialogStateVariant = UpdateStateVariant.CREATE_TASK.name
                successDialogState = true
            }
        }
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
                task?.let {
                    scheduleReminderWithWorkManager(ctx, it)
                }
                successDialogStateVariant = UpdateStateVariant.UPDATE_TASK.name
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
                .fillMaxSize()
        ) {
            Text(
                "Task Name",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
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
                color = MaterialTheme.colorScheme.onBackground,
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
                            containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
            Text(
                "Date & Time",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
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
                        selectedDate = ""
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
                        color = MaterialTheme.colorScheme.onBackground,
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
                        color = MaterialTheme.colorScheme.onBackground,
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
                color = MaterialTheme.colorScheme.onBackground,
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
                            timeRange = "$startTime - $endTime",
                            date = selectedDate.toLocalDate()
                        )
                        if (taskString.isNullOrEmpty().not()) {
                            vm.updateTask(task)
                        } else {
                            vm.insertTask(task)
                        }

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
                    containerColor = MaterialTheme.colorScheme.secondary,
                )
            ) {
                Text(
                    "Create Task", fontSize = 16.sp, fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondary,
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
fun String.toLocalDate(): LocalDate? {
    return try {
        val formats = listOf(
            "MM/dd/yyyy",
            "M/d/yyyy",
            "yyyy-MM-dd",
            "dd/MM/yyyy"
        )

        for (pattern in formats) {
            try {
                val formatter = DateTimeFormatter.ofPattern(pattern)
                return LocalDate.parse(this.trim(), formatter)
            } catch (e: DateTimeParseException) {
                // Try next format
            }
        }
        null
    } catch (e: Exception) {
        null
    }
}