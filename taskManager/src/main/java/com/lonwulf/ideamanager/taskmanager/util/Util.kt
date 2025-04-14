package com.lonwulf.ideamanager.taskmanager.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.lonwulf.ideamanager.core.domain.model.TaskItem
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun dpToPx(dp: Dp): Float {
    val density = LocalDensity.current
    return with(density) { dp.toPx() }
}
@RequiresApi(Build.VERSION_CODES.O)
fun TaskItem.toReminderMillis(): Long? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
        val startTime = timeRange?.split("-")?.firstOrNull()?.trim()
        val localTime = startTime?.let { LocalTime.parse(it, formatter) } ?: return null
        val dateTime = LocalDateTime.of(date, localTime)
        val zonedDateTime = dateTime.atZone(ZoneId.systemDefault())
        zonedDateTime.toInstant().toEpochMilli()
    } catch (e: Exception) {
        null
    }
}