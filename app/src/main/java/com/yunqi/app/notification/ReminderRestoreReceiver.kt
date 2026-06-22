package com.yunqi.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yunqi.app.data.local.ReminderSettingsRepository
import com.yunqi.app.data.local.record.CalendarRecordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val REMINDER_RESTORE_TAG = "ReminderRestore"

/**
 * Restores local reminder work after device boot or app package replacement.
 */
class ReminderRestoreReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in RESTORE_ACTIONS) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                runCatching {
                    ReminderRestorer(context.applicationContext).restore()
                }.onFailure { error ->
                    Log.w(REMINDER_RESTORE_TAG, "Unable to restore local reminders.", error)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        val RESTORE_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
        )
    }
}

internal class ReminderRestorer(
    context: Context,
    private val todayProvider: () -> LocalDate = { LocalDate.now() },
) {
    private val reminderSettingsRepository = ReminderSettingsRepository(context)
    private val calendarRecordRepository = CalendarRecordRepository(context)
    private val dailyReminderScheduler = DailyReminderScheduler(context)
    private val appointmentReminderScheduler = AppointmentReminderScheduler(context)

    /**
     * Rebuilds WorkManager jobs from local settings and future appointment records.
     */
    suspend fun restore() {
        val settings = reminderSettingsRepository.reminderSettingsFlow.first()

        settings.dailyReminders.forEach { reminder ->
            if (reminder.enabled) {
                dailyReminderScheduler.schedule(
                    type = reminder.type,
                    time = reminder.time,
                    customMessage = reminder.customMessage,
                )
            } else {
                dailyReminderScheduler.cancel(reminder.type)
            }
        }

        if (settings.appointmentRemindersEnabled) {
            appointmentReminderScheduler.cancelAll()
            calendarRecordRepository
                .futureAppointmentRecords(todayProvider())
                .forEach(appointmentReminderScheduler::schedule)
        } else {
            appointmentReminderScheduler.cancelAll()
        }
    }
}
