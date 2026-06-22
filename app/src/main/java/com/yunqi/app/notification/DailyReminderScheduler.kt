package com.yunqi.app.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yunqi.app.domain.reminder.DailyReminderType
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

private const val LEGACY_DAILY_REMINDER_WORK_NAME = "daily-record-reminder"
private const val DAILY_REMINDER_WORK_NAME_PREFIX = "daily-record-reminder"

class DailyReminderScheduler(
    context: Context,
    private val nowProvider: () -> LocalDateTime = { LocalDateTime.now() },
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedules one local daily reminder type when the requested HH:mm time is valid.
     */
    fun schedule(type: DailyReminderType, time: String, customMessage: String = "") {
        val delayMillis = calculateDailyReminderInitialDelay(time, nowProvider()) ?: return
        cancelLegacy()
        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                Data.Builder()
                    .putString(DailyReminderWorker.KEY_REMINDER_TYPE, type.name)
                    .putString(DailyReminderWorker.KEY_CUSTOM_MESSAGE, customMessage.trim())
                    .build(),
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            dailyReminderWorkName(type),
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request,
        )
    }

    fun cancel(type: DailyReminderType) {
        workManager.cancelUniqueWork(dailyReminderWorkName(type))
    }

    fun cancelAll() {
        cancelLegacy()
        DailyReminderType.entries.forEach(::cancel)
    }

    fun cancelLegacy() {
        workManager.cancelUniqueWork(LEGACY_DAILY_REMINDER_WORK_NAME)
    }
}

internal fun dailyReminderWorkName(type: DailyReminderType): String =
    "$DAILY_REMINDER_WORK_NAME_PREFIX-${type.name.lowercase()}"

internal fun calculateDailyReminderInitialDelay(time: String, now: LocalDateTime): Long? {
    val reminderTime = runCatching { LocalTime.parse(time.trim()) }.getOrNull() ?: return null
    var nextReminderAt = LocalDateTime.of(now.toLocalDate(), reminderTime)
    if (!nextReminderAt.isAfter(now)) {
        nextReminderAt = nextReminderAt.plusDays(1)
    }
    return Duration.between(now, nextReminderAt).toMillis()
}
