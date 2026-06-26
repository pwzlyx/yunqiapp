package com.yunqi.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yunqi.app.data.local.DailyReminderPreference
import com.yunqi.app.data.local.ReminderSettingsRepository
import com.yunqi.app.data.local.record.CalendarRecordRepository
import com.yunqi.app.domain.reminder.DailyReminderType
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
        if (!shouldRestoreRemindersForAction(intent.action)) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                runCatching {
                    ReminderRestorer(context.applicationContext).restore()
                }.onFailure { error ->
                    Log.w(REMINDER_RESTORE_TAG, reminderRestoreFailureLogMessage(error))
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

internal fun shouldRestoreRemindersForAction(action: String?): Boolean = action in restoreReminderActions

private val restoreReminderActions = setOf(
    Intent.ACTION_BOOT_COMPLETED,
    Intent.ACTION_MY_PACKAGE_REPLACED,
    Intent.ACTION_TIMEZONE_CHANGED,
    Intent.ACTION_TIME_CHANGED,
)

/**
 * Builds a privacy-safe restore failure message without exception text or stack traces.
 */
internal fun reminderRestoreFailureLogMessage(error: Throwable): String =
    "Unable to restore local reminders. Cause=${error::class.java.simpleName}"

internal data class DailyReminderRestorePlan(
    val cancelLegacyWork: Boolean,
    val remindersToSchedule: List<DailyReminderPreference>,
    val reminderTypesToCancel: List<DailyReminderType>,
)

/**
 * Describes how local daily reminder work should be rebuilt after boot or app upgrade.
 */
internal fun dailyReminderRestorePlan(reminders: List<DailyReminderPreference>): DailyReminderRestorePlan =
    DailyReminderRestorePlan(
        cancelLegacyWork = true,
        remindersToSchedule = reminders.filter { it.enabled },
        reminderTypesToCancel = reminders.filterNot { it.enabled }.map { it.type },
    )

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
        val dailyPlan = dailyReminderRestorePlan(settings.dailyReminders)

        if (dailyPlan.cancelLegacyWork) {
            dailyReminderScheduler.cancelLegacy()
        }
        dailyPlan.remindersToSchedule.forEach { reminder ->
            dailyReminderScheduler.schedule(
                type = reminder.type,
                time = reminder.time,
                customMessage = reminder.customMessage,
            )
        }
        dailyPlan.reminderTypesToCancel.forEach { type ->
            dailyReminderScheduler.cancel(type)
        }

        if (settings.appointmentRemindersEnabled) {
            appointmentReminderScheduler.cancelAll()
            calendarRecordRepository
                .futureAppointmentRecords(todayProvider())
                .forEach { record ->
                    appointmentReminderScheduler.schedule(record, settings.appointmentReminderLeadMinutes)
                }
        } else {
            appointmentReminderScheduler.cancelAll()
        }
    }
}
