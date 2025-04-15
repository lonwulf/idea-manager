package com.lonwulf.ideamanager.taskmanager.worker

import android.app.PendingIntent
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
        val taskId = inputData.getInt("TASK_ID", 0)
        val title = inputData.getString("TASK_TITLE") ?: "Task Reminder"
        val description = inputData.getString("TASK_DESCRIPTION") ?: ""

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.bell)
            .setContentTitle("Reminder")
            .setContentText("Upcoming task: $title")
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            return if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(taskId, builder.build())
                Result.success()
            } else {
                Log.w("ReminderWorker", "POST_NOTIFICATIONS permission not granted")
                Result.failure()
            }
        }
    }
}
