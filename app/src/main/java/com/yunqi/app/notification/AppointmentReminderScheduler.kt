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

private const val REMINDER_LEAD_MINUTES = 60L

class AppointmentReminderScheduler(
    context: Context,
    private val nowProvider: () -> LocalDateTime = { LocalDateTime.now() },
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedules an appointment reminder when the record has a future date and HH:mm time.
     */
    fun schedule(record: CalendarRecord) {
        val plan = record.toAppointmentReminderPlan(nowProvider()) ?: return

        val request = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
            .setInitialDelay(plan.delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                Data.Builder()
                    .putString(AppointmentReminderWorker.KEY_RECORD_ID, record.id)
                    .putString(AppointmentReminderWorker.KEY_APPOINTMENT_LABEL, plan.appointmentLabel)
                    .build(),
            )
            .build()

        workManager.enqueueUniqueWork(
            workNameFor(record.id),
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun cancel(recordId: String) {
        workManager.cancelUniqueWork(workNameFor(recordId))
    }

    private fun workNameFor(recordId: String): String = "appointment-reminder-$recordId"
}

internal data class AppointmentReminderPlan(
    val delayMillis: Long,
    val appointmentLabel: String,
)

/**
 * Calculates reminder metadata without touching Android APIs, which keeps scheduling rules testable.
 */
internal fun CalendarRecord.toAppointmentReminderPlan(now: LocalDateTime): AppointmentReminderPlan? {
    if (type != CalendarRecordType.Appointment) return null

    val appointmentTime = appointmentTime?.toLocalTimeOrNull() ?: return null
    val appointmentDateTime = LocalDateTime.of(date, appointmentTime)
    val reminderDateTime = appointmentDateTime.minusMinutes(REMINDER_LEAD_MINUTES)
    val delayMillis = Duration.between(now, reminderDateTime).toMillis()
    if (delayMillis <= 0) return null

    val label = listOfNotNull(
        this.appointmentTime,
        appointmentLocation,
    ).joinToString(" ")
    return AppointmentReminderPlan(
        delayMillis = delayMillis,
        appointmentLabel = label,
    )
}

private fun String.toLocalTimeOrNull(): LocalTime? = runCatching {
    LocalTime.parse(trim())
}.getOrNull()
