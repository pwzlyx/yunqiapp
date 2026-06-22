package com.yunqi.app.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * Checks both the Android 13 runtime permission and the app-level notification switch.
 */
fun canPostYunqiNotifications(context: Context): Boolean {
    return hasYunqiNotificationRuntimePermission(context) &&
        NotificationManagerCompat.from(context).areNotificationsEnabled()
}

fun hasYunqiNotificationRuntimePermission(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
