package com.yunqi.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.yunqi.app.R

object YunqiNotificationChannels {
    const val APPOINTMENTS_CHANNEL_ID = "appointments"

    /**
     * Creates notification channels required by scheduled local reminders.
     */
    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            APPOINTMENTS_CHANNEL_ID,
            context.getString(R.string.notification_channel_appointments),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        notificationManager.createNotificationChannel(channel)
    }
}

