package com.yunqi.app.notification

import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yunqi.app.R

class AppointmentReminderWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if (!canPostYunqiNotifications(context)) return Result.success()

        val recordId = inputData.getString(KEY_RECORD_ID).orEmpty()
        val content = appointmentReminderNotificationContent(
            inputData.getString(KEY_APPOINTMENT_LABEL).orEmpty(),
        )
        val notification = NotificationCompat.Builder(
            context,
            YunqiNotificationChannels.APPOINTMENTS_CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_appointment_title))
            .setContentText(content.bodyText(context))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(yunqiLaunchPendingIntent(context))
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(appointmentReminderNotificationId(recordId), notification)
        return Result.success()
    }

    companion object {
        const val KEY_RECORD_ID = "record_id"
        const val KEY_APPOINTMENT_LABEL = "appointment_label"
    }
}

/**
 * Normalizes record IDs into stable non-negative notification IDs, including the Int.MIN_VALUE hash edge case.
 */
internal fun appointmentReminderNotificationId(recordId: String): Int =
    recordId.hashCode().toNonNegativeNotificationId()

internal fun Int.toNonNegativeNotificationId(): Int = this and Int.MAX_VALUE

internal data class AppointmentReminderNotificationContent(
    @StringRes val bodyResId: Int,
    val label: String?,
) {
    fun bodyText(context: Context): String =
        label?.let { context.getString(bodyResId, it) } ?: context.getString(bodyResId)
}

internal fun appointmentReminderNotificationContent(
    appointmentLabel: String,
): AppointmentReminderNotificationContent {
    val trimmedLabel = appointmentLabel.trim()
    return if (trimmedLabel.isBlank()) {
        AppointmentReminderNotificationContent(
            bodyResId = R.string.notification_appointment_body_without_label,
            label = null,
        )
    } else {
        AppointmentReminderNotificationContent(
            bodyResId = R.string.notification_appointment_body,
            label = trimmedLabel,
        )
    }
}
