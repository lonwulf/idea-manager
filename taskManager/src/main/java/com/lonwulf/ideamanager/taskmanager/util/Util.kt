package com.lonwulf.ideamanager.taskmanager.util

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import com.lonwulf.ideamanager.taskmanager.worker.ReminderWorker
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun dpToPx(dp: Dp): Float {
    val density = LocalDensity.current
    return with(density) { dp.toPx() }
}

@RequiresApi(Build.VERSION_CODES.O)
fun TaskItem.toReminderMillis(): Long? {
    if (date == null || timeRange.isNullOrEmpty()) return null

    return try {
        val timeFormats = listOf(
            "h:mm a",
            "H:mm",
            "ha",
            "H"
        )

        val startTime = timeRange!!.split("-").firstOrNull()?.trim() ?: return null

        var localTime: LocalTime? = null
        for (format in timeFormats) {
            try {
                val formatter = DateTimeFormatter.ofPattern(format, Locale.getDefault())
                localTime = LocalTime.parse(startTime, formatter)
                break
            } catch (e: DateTimeParseException) {
                // Try next format
            }
        }
        if (localTime == null) return null
        val dateTime = LocalDateTime.of(date, localTime)
        val zonedDateTime = dateTime.atZone(ZoneId.systemDefault())
        zonedDateTime.toInstant().toEpochMilli()
    } catch (e: Exception) {
        Log.e("TaskItem", "Error converting to reminder time", e)
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun scheduleReminderWithWorkManager(context: Context, task: TaskItem) {
    val reminderTimeMillis = task.toReminderMillis() ?: return
    val delayMillis = reminderTimeMillis - System.currentTimeMillis()

    if (delayMillis <= 0) return

    val workName = "reminder_task_${task.id}"

    val inputData = Data.Builder()
        .putInt("TASK_ID", task.id ?: 0)
        .putString("TASK_TITLE", task.title)
        .putString("TASK_DESCRIPTION", task.description ?: "")
        .build()

    val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .addTag("task_reminder")
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        workName,
        ExistingWorkPolicy.REPLACE,
        workRequest
    )

    Log.d(
        "TaskReminder",
        "Scheduled reminder for task ${task.title} at ${Date(reminderTimeMillis)}"
    )
}
fun cancelReminder(context: Context, taskId: Int) {
    val uniqueWorkName = "reminder_task_$taskId"
    WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
}