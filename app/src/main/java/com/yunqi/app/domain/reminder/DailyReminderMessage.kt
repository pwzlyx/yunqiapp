package com.yunqi.app.domain.reminder

const val MAX_DAILY_REMINDER_CUSTOM_MESSAGE_LENGTH = 160

/**
 * Keeps user-authored reminder text small enough for WorkManager input data and notification display.
 */
internal fun String.sanitizeDailyReminderCustomMessage(): String =
    trim().take(MAX_DAILY_REMINDER_CUSTOM_MESSAGE_LENGTH)
