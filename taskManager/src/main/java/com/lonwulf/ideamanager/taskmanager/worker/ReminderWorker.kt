package com.lonwulf.ideamanager.taskmanager.worker

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.lonwulf.ideamanager.taskmanager.R

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString("TASK_TITLE") ?: "Task Reminder"

        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.bell)
            .setContentTitle("Reminder")
            .setContentText("Upcoming task: $title")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                notify(title.hashCode(), builder.build())
            } else {
                Log.w("ReminderWorker", "POST_NOTIFICATIONS permission not granted")
            }
        }

        return Result.success()
    }
}
