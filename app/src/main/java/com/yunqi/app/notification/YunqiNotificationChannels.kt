package com.yunqi.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.yunqi.app.R

object YunqiNotificationChannels {
    const val APPOINTMENTS_CHANNEL_ID = "appointments"
    const val DAILY_REMINDERS_CHANNEL_ID = "daily_reminders"

    /**
     * Creates notification channels required by scheduled local reminders.
     */
    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channels = listOf(
            NotificationChannel(
                APPOINTMENTS_CHANNEL_ID,
                context.getString(R.string.notification_channel_appointments),
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
            NotificationChannel(
                DAILY_REMINDERS_CHANNEL_ID,
                context.getString(R.string.notification_channel_daily_reminders),
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
        channels.forEach(notificationManager::createNotificationChannel)
    }
}
