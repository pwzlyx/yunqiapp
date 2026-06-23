package com.yunqi.app.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

private const val DEFAULT_REMINDER_LEAD_MINUTES = 60L
private const val APPOINTMENT_REMINDER_TAG = "appointment_reminder"

class AppointmentReminderScheduler(
    context: Context,
    private val nowProvider: () -> LocalDateTime = { LocalDateTime.now() },
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedules an appointment reminder when the record has a future date and HH:mm time.
     */
    fun schedule(record: CalendarRecord, leadMinutes: Long = DEFAULT_REMINDER_LEAD_MINUTES) {
        when (val decision = record.toAppointmentReminderScheduleDecision(nowProvider(), leadMinutes)) {
            is AppointmentReminderScheduleDecision.Cancel -> {
                cancel(decision.recordId)
                return
            }

            is AppointmentReminderScheduleDecision.Schedule -> {
                val request = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                    .setInitialDelay(decision.plan.delayMillis, TimeUnit.MILLISECONDS)
                    .addTag(APPOINTMENT_REMINDER_TAG)
                    .setInputData(
                        Data.Builder()
                            .putString(AppointmentReminderWorker.KEY_RECORD_ID, decision.recordId)
                            .putString(AppointmentReminderWorker.KEY_APPOINTMENT_LABEL, decision.plan.appointmentLabel)
                            .build(),
                    )
                    .build()

                workManager.enqueueUniqueWork(
                    workNameFor(decision.recordId),
                    ExistingWorkPolicy.REPLACE,
                    request,
                )
            }
        }
    }

    fun cancel(recordId: String) {
        workManager.cancelUniqueWork(workNameFor(recordId))
    }

    fun cancelAll() {
        workManager.cancelAllWorkByTag(APPOINTMENT_REMINDER_TAG)
    }

    private fun workNameFor(recordId: String): String = "appointment-reminder-$recordId"
}

internal data class AppointmentReminderPlan(
    val delayMillis: Long,
    val appointmentLabel: String,
)

internal sealed interface AppointmentReminderScheduleDecision {
    data class Schedule(
        val recordId: String,
        val plan: AppointmentReminderPlan,
    ) : AppointmentReminderScheduleDecision

    data class Cancel(
        val recordId: String,
    ) : AppointmentReminderScheduleDecision
}

internal fun CalendarRecord.toAppointmentReminderScheduleDecision(
    now: LocalDateTime,
    leadMinutes: Long = DEFAULT_REMINDER_LEAD_MINUTES,
): AppointmentReminderScheduleDecision {
    val plan = toAppointmentReminderPlan(now, leadMinutes)
    return if (plan == null) {
        AppointmentReminderScheduleDecision.Cancel(id)
    } else {
        AppointmentReminderScheduleDecision.Schedule(id, plan)
    }
}

/**
 * Calculates reminder metadata without touching Android APIs, which keeps scheduling rules testable.
 */
internal fun CalendarRecord.toAppointmentReminderPlan(
    now: LocalDateTime,
    leadMinutes: Long = DEFAULT_REMINDER_LEAD_MINUTES,
): AppointmentReminderPlan? {
    if (type != CalendarRecordType.Appointment) return null

    val appointmentTime = appointmentTime?.toLocalTimeOrNull() ?: return null
    val appointmentDateTime = LocalDateTime.of(date, appointmentTime)
    if (!appointmentDateTime.isAfter(now)) return null

    val reminderDateTime = appointmentDateTime.minusMinutes(leadMinutes.coerceAtLeast(0L))
    val delayMillis = Duration.between(now, reminderDateTime).toMillis().coerceAtLeast(0L)

    val label = listOfNotNull(
        this.appointmentTime,
        appointmentLocation,
        appointmentDoctor,
        appointmentItems,
    )
        .map(String::trim)
        .filter(String::isNotBlank)
        .joinToString(" ")
    return AppointmentReminderPlan(
        delayMillis = delayMillis,
        appointmentLabel = label,
    )
}

private fun String.toLocalTimeOrNull(): LocalTime? = runCatching {
    LocalTime.parse(trim())
}.getOrNull()
