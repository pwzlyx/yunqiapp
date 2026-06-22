package com.yunqi.app.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
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
        if (!canPostYunqiNotifications(context)) return Result.success()

        val reminderType = inputData.getString(KEY_REMINDER_TYPE)
            ?.let { runCatching { DailyReminderType.valueOf(it) }.getOrNull() }
            ?: DailyReminderType.Custom
        val customMessage = inputData.getString(KEY_CUSTOM_MESSAGE).orEmpty()
        val content = reminderType.notificationContent(customMessage)
        val notification = NotificationCompat.Builder(
            context,
            YunqiNotificationChannels.DAILY_REMINDERS_CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(content.titleResId))
            .setContentText(content.bodyText ?: context.getString(content.bodyResId))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(yunqiLaunchPendingIntent(context))
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(DAILY_REMINDER_NOTIFICATION_ID_BASE + reminderType.ordinal, notification)
        return Result.success()
    }

    companion object {
        const val KEY_REMINDER_TYPE = "reminder_type"
        const val KEY_CUSTOM_MESSAGE = "custom_message"
    }
}

internal data class DailyReminderNotificationContent(
    val titleResId: Int,
    val bodyResId: Int,
    val bodyText: String? = null,
)

internal fun DailyReminderType.notificationContent(
    customMessage: String,
): DailyReminderNotificationContent = when (this) {
    DailyReminderType.Weight -> DailyReminderNotificationContent(
        titleResId = R.string.notification_daily_weight_title,
        bodyResId = R.string.notification_daily_weight_body,
    )

    DailyReminderType.FetalMovement -> DailyReminderNotificationContent(
        titleResId = R.string.notification_daily_fetal_movement_title,
        bodyResId = R.string.notification_daily_fetal_movement_body,
    )

    DailyReminderType.Vitamin -> DailyReminderNotificationContent(
        titleResId = R.string.notification_daily_vitamin_title,
        bodyResId = R.string.notification_daily_vitamin_body,
    )

    DailyReminderType.Water -> DailyReminderNotificationContent(
        titleResId = R.string.notification_daily_water_title,
        bodyResId = R.string.notification_daily_water_body,
    )

    DailyReminderType.Exercise -> DailyReminderNotificationContent(
        titleResId = R.string.notification_daily_exercise_title,
        bodyResId = R.string.notification_daily_exercise_body,
    )

    DailyReminderType.Custom -> DailyReminderNotificationContent(
        titleResId = R.string.notification_daily_custom_title,
        bodyResId = R.string.notification_daily_custom_body,
        bodyText = customMessage.trim().takeIf(String::isNotBlank),
    )
}
