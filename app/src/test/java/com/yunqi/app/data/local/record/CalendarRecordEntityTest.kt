package com.yunqi.app.data.local.record

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalendarRecordEntityTest {
    @Test
    fun `calendar record entity round trip preserves every structured field`() {
        val record = CalendarRecord(
            id = "record-1",
            date = LocalDate.of(2026, 6, 22),
            type = CalendarRecordType.Appointment,
            note = "Bring previous report",
            weightKg = 58.4,
            fetalMovementCount = 12,
            fetalMovementPeriod = "evening",
            fetalMovementFeeling = "active after dinner",
            symptomType = "headache",
            symptomSeverity = "mild",
            exerciseType = "walking",
            exerciseMinutes = 30,
            exerciseIntensity = "low",
            dietMeal = "lunch",
            dietContent = "rice, fish, vegetables",
            appointmentTime = "09:30",
            appointmentLocation = "City Hospital",
            appointmentDoctor = "Dr Chen",
            appointmentItems = "blood test",
            appointmentResult = "normal",
            createdAtEpochMillis = 42L,
        )

        val roundTrippedRecord = record.toEntity().toDomain()

        assertEquals(record, roundTrippedRecord)
    }

    @Test
    fun `calendar record entity stores stable date and type values`() {
        val record = CalendarRecord(
            id = "record-1",
            date = LocalDate.of(2026, 6, 22),
            type = CalendarRecordType.FetalMovement,
            note = "",
            weightKg = null,
            fetalMovementCount = 8,
            fetalMovementPeriod = null,
            fetalMovementFeeling = null,
            exerciseMinutes = null,
            appointmentTime = null,
            appointmentLocation = null,
            appointmentDoctor = null,
            appointmentItems = null,
            appointmentResult = null,
            createdAtEpochMillis = 42L,
        )

        val entity = record.toEntity()

        assertEquals("2026-06-22", entity.date)
        assertEquals("FetalMovement", entity.type)
    }

    @Test
    fun `calendar record entity safe conversion skips corrupt stored date`() {
        val entity = validEntity().copy(date = "2026/06/22")

        assertNull(entity.toDomainOrNull())
    }

    @Test
    fun `calendar record entity safe conversion skips unknown stored type`() {
        val entity = validEntity().copy(type = "LegacyAppointment")

        assertNull(entity.toDomainOrNull())
    }

    private fun validEntity(): CalendarRecordEntity = CalendarRecord(
        id = "record-1",
        date = LocalDate.of(2026, 6, 22),
        type = CalendarRecordType.Appointment,
        note = "",
        weightKg = null,
        fetalMovementCount = null,
        fetalMovementPeriod = null,
        fetalMovementFeeling = null,
        exerciseMinutes = null,
        appointmentTime = "09:30",
        appointmentLocation = "City Hospital",
        appointmentDoctor = null,
        appointmentItems = null,
        appointmentResult = null,
        createdAtEpochMillis = 42L,
    ).toEntity()
}
