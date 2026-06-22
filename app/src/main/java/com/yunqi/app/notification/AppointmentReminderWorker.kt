package com.yunqi.app.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yunqi.app.R
import kotlin.math.absoluteValue

class AppointmentReminderWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if (!canPostYunqiNotifications(context)) return Result.success()

        val recordId = inputData.getString(KEY_RECORD_ID).orEmpty()
        val appointmentLabel = inputData.getString(KEY_APPOINTMENT_LABEL).orEmpty()
        val notification = NotificationCompat.Builder(
            context,
            YunqiNotificationChannels.APPOINTMENTS_CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_appointment_title))
            .setContentText(context.getString(R.string.notification_appointment_body, appointmentLabel))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(yunqiLaunchPendingIntent(context))
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(recordId.hashCode().absoluteValue, notification)
        return Result.success()
    }

    companion object {
        const val KEY_RECORD_ID = "record_id"
        const val KEY_APPOINTMENT_LABEL = "appointment_label"
    }
}
