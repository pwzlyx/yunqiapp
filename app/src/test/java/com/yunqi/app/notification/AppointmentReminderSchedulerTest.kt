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
    fun `ignores appointment when reminder time has passed`() {
        val record = appointmentRecord(
            date = LocalDate.of(2026, 7, 1),
            time = "14:30",
        )

        val plan = record.toAppointmentReminderPlan(
            now = LocalDateTime.of(2026, 7, 1, 14, 0),
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
        exerciseMinutes = null,
        appointmentTime = time,
        appointmentLocation = location,
        appointmentDoctor = null,
        appointmentItems = null,
        appointmentResult = null,
        createdAtEpochMillis = 0L,
    )
}
