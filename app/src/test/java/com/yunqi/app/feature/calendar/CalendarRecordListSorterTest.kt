package com.yunqi.app.feature.calendar

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class CalendarRecordListSorterTest {
    @Test
    fun `sorts selected day records by appointment time then local recency`() {
        val records = listOf(
            record(id = "weight", type = CalendarRecordType.Weight, createdAtEpochMillis = 300L),
            record(
                id = "appointment-untimed",
                type = CalendarRecordType.Appointment,
                appointmentTime = "",
                createdAtEpochMillis = 200L,
            ),
            record(
                id = "appointment-afternoon",
                type = CalendarRecordType.Appointment,
                appointmentTime = "14:00",
                createdAtEpochMillis = 100L,
            ),
            record(
                id = "appointment-morning",
                type = CalendarRecordType.Appointment,
                appointmentTime = "09:00",
                createdAtEpochMillis = 50L,
            ),
            record(id = "note", type = CalendarRecordType.Note, createdAtEpochMillis = 400L),
        )

        val sortedIds = CalendarRecordListSorter.sort(records).map(CalendarRecord::id)

        assertEquals(
            listOf(
                "appointment-morning",
                "appointment-afternoon",
                "appointment-untimed",
                "note",
                "weight",
            ),
            sortedIds,
        )
    }

    @Test
    fun `treats legacy invalid appointment time as untimed`() {
        val records = listOf(
            record(
                id = "appointment-invalid-time",
                type = CalendarRecordType.Appointment,
                appointmentTime = "9:00",
                createdAtEpochMillis = 200L,
            ),
            record(
                id = "appointment-valid-time",
                type = CalendarRecordType.Appointment,
                appointmentTime = "10:00",
                createdAtEpochMillis = 100L,
            ),
        )

        val sortedIds = CalendarRecordListSorter.sort(records).map(CalendarRecord::id)

        assertEquals(
            listOf("appointment-valid-time", "appointment-invalid-time"),
            sortedIds,
        )
    }

    private fun record(
        id: String,
        type: CalendarRecordType,
        appointmentTime: String? = null,
        createdAtEpochMillis: Long,
    ): CalendarRecord = CalendarRecord(
        id = id,
        date = LocalDate.of(2026, 6, 21),
        type = type,
        note = "",
        weightKg = null,
        fetalMovementCount = null,
        fetalMovementPeriod = null,
        fetalMovementFeeling = null,
        exerciseMinutes = null,
        appointmentTime = appointmentTime,
        appointmentLocation = null,
        appointmentDoctor = null,
        appointmentItems = null,
        appointmentResult = null,
        createdAtEpochMillis = createdAtEpochMillis,
    )
}
