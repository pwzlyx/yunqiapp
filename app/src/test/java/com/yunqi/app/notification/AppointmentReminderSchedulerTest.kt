package com.yunqi.app.notification

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppointmentReminderSchedulerTest {
    @Test
    fun `creates reminder plan one hour before future appointment`() {
        val record = appointmentRecord(
            date = LocalDate.of(2026, 7, 1),
            time = "14:30",
            location = "City Hospital",
        )

        val plan = record.toAppointmentReminderPlan(
            now = LocalDateTime.of(2026, 7, 1, 12, 30),
        )

        assertEquals(60L * 60L * 1000L, plan?.delayMillis)
        assertEquals("14:30 City Hospital", plan?.appointmentLabel)
    }

    @Test
    fun `uses immediate reminder when appointment is still future but lead time has passed`() {
        val record = appointmentRecord(
            date = LocalDate.of(2026, 7, 1),
            time = "14:30",
        )

        val plan = record.toAppointmentReminderPlan(
            now = LocalDateTime.of(2026, 7, 1, 14, 0),
        )

        assertEquals(0L, plan?.delayMillis)
    }

    @Test
    fun `ignores appointment when appointment time has passed`() {
        val record = appointmentRecord(
            date = LocalDate.of(2026, 7, 1),
            time = "14:30",
        )

        val plan = record.toAppointmentReminderPlan(
            now = LocalDateTime.of(2026, 7, 1, 14, 30),
        )

        assertNull(plan)
    }

    @Test
    fun `ignores appointment without valid time`() {
        val record = appointmentRecord(
            date = LocalDate.of(2026, 7, 1),
            time = "bad time",
        )

        val plan = record.toAppointmentReminderPlan(
            now = LocalDateTime.of(2026, 7, 1, 12, 0),
        )

        assertNull(plan)
    }

    @Test
    fun `ignores appointment without time`() {
        val record = appointmentRecord(
            date = LocalDate.of(2026, 7, 1),
            time = "",
        )

        val plan = record.toAppointmentReminderPlan(
            now = LocalDateTime.of(2026, 7, 1, 12, 0),
        )

        assertNull(plan)
    }

    @Test
    fun `chooses cancel decision when appointment no longer has a reminder plan`() {
        val record = appointmentRecord(
            date = LocalDate.of(2026, 7, 1),
            time = "",
        )

        val decision = record.toAppointmentReminderScheduleDecision(
            now = LocalDateTime.of(2026, 7, 1, 12, 0),
        )

        assertEquals(AppointmentReminderScheduleDecision.Cancel("appointment-1"), decision)
    }

    @Test
    fun `chooses schedule decision when appointment has a reminder plan`() {
        val record = appointmentRecord(
            date = LocalDate.of(2026, 7, 1),
            time = "14:30",
        )

        val decision = record.toAppointmentReminderScheduleDecision(
            now = LocalDateTime.of(2026, 7, 1, 12, 30),
        )

        assertEquals(
            AppointmentReminderScheduleDecision.Schedule(
                recordId = "appointment-1",
                plan = AppointmentReminderPlan(
                    delayMillis = 60L * 60L * 1000L,
                    appointmentLabel = "14:30",
                ),
            ),
            decision,
        )
    }

    @Test
    fun `ignores non appointment records`() {
        val record = appointmentRecord(
            date = LocalDate.of(2026, 7, 1),
            time = "14:30",
        ).copy(type = CalendarRecordType.Weight)

        val plan = record.toAppointmentReminderPlan(
            now = LocalDateTime.of(2026, 7, 1, 12, 0),
        )

        assertNull(plan)
    }

    private fun appointmentRecord(
        date: LocalDate,
        time: String,
        location: String? = null,
    ) = CalendarRecord(
        id = "appointment-1",
        date = date,
        type = CalendarRecordType.Appointment,
        note = "",
        weightKg = null,
        fetalMovementCount = null,
        fetalMovementPeriod = null,
        fetalMovementFeeling = null,
        exerciseMinutes = null,
        appointmentTime = time,
        appointmentLocation = location,
        appointmentDoctor = null,
        appointmentItems = null,
        appointmentResult = null,
        createdAtEpochMillis = 0L,
    )
}
