package com.yunqi.app.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.yunqi.app.MainActivity

private const val YUNQI_LAUNCH_REQUEST_CODE = 20260622

/**
 * Creates the app launch action used by local pregnancy reminder notifications.
 */
internal fun yunqiLaunchPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    return PendingIntent.getActivity(
        context,
        YUNQI_LAUNCH_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
