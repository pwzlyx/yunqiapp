package com.yunqi.app.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

private const val DAILY_REMINDER_WORK_NAME = "daily-record-reminder"

class DailyReminderScheduler(
    context: Context,
    private val nowProvider: () -> LocalDateTime = { LocalDateTime.now() },
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedules one local daily reminder when the requested HH:mm time is valid.
     */
    fun schedule(time: String) {
        val delayMillis = calculateDailyReminderInitialDelay(time, nowProvider()) ?: return
        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            DAILY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request,
        )
    }

    fun cancel() {
        workManager.cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
    }
}

internal fun calculateDailyReminderInitialDelay(time: String, now: LocalDateTime): Long? {
    val reminderTime = runCatching { LocalTime.parse(time.trim()) }.getOrNull() ?: return null
    var nextReminderAt = LocalDateTime.of(now.toLocalDate(), reminderTime)
    if (!nextReminderAt.isAfter(now)) {
        nextReminderAt = nextReminderAt.plusDays(1)
    }
    return Duration.between(now, nextReminderAt).toMillis()
}
