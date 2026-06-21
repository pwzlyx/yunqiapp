package com.yunqi.app.notification

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yunqi.app.R
import com.yunqi.app.domain.reminder.DailyReminderType

private const val DAILY_REMINDER_NOTIFICATION_ID_BASE = 20260621

class DailyReminderWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if (!canPostNotifications()) return Result.success()

        val reminderType = inputData.getString(KEY_REMINDER_TYPE)
            ?.let { runCatching { DailyReminderType.valueOf(it) }.getOrNull() }
            ?: DailyReminderType.Custom
        val notification = NotificationCompat.Builder(
            context,
            YunqiNotificationChannels.DAILY_REMINDERS_CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(reminderType.notificationTitleResId()))
            .setContentText(context.getString(reminderType.notificationBodyResId()))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(DAILY_REMINDER_NOTIFICATION_ID_BASE + reminderType.ordinal, notification)
        return Result.success()
    }

    private fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val KEY_REMINDER_TYPE = "reminder_type"
    }
}

private fun DailyReminderType.notificationTitleResId(): Int = when (this) {
    DailyReminderType.Weight -> R.string.notification_daily_weight_title
    DailyReminderType.FetalMovement -> R.string.notification_daily_fetal_movement_title
    DailyReminderType.Vitamin -> R.string.notification_daily_vitamin_title
    DailyReminderType.Water -> R.string.notification_daily_water_title
    DailyReminderType.Exercise -> R.string.notification_daily_exercise_title
    DailyReminderType.Custom -> R.string.notification_daily_custom_title
}

private fun DailyReminderType.notificationBodyResId(): Int = when (this) {
    DailyReminderType.Weight -> R.string.notification_daily_weight_body
    DailyReminderType.FetalMovement -> R.string.notification_daily_fetal_movement_body
    DailyReminderType.Vitamin -> R.string.notification_daily_vitamin_body
    DailyReminderType.Water -> R.string.notification_daily_water_body
    DailyReminderType.Exercise -> R.string.notification_daily_exercise_body
    DailyReminderType.Custom -> R.string.notification_daily_custom_body
}
