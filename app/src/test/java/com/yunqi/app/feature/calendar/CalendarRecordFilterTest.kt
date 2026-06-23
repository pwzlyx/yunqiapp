package com.yunqi.app.feature.calendar

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class CalendarRecordFilterTest {
    @Test
    fun `returns all records when no type filter is selected`() {
        val records = listOf(
            record("appointment", CalendarRecordType.Appointment),
            record("weight", CalendarRecordType.Weight),
        )

        val filteredRecords = filterCalendarRecords(
            records = records,
            recordFilter = null,
        )

        assertEquals(records, filteredRecords)
    }

    @Test
    fun `returns only records matching the selected type while preserving order`() {
        val firstWeight = record("weight-1", CalendarRecordType.Weight)
        val appointment = record("appointment", CalendarRecordType.Appointment)
        val secondWeight = record("weight-2", CalendarRecordType.Weight)

        val filteredRecords = filterCalendarRecords(
            records = listOf(firstWeight, appointment, secondWeight),
            recordFilter = CalendarRecordType.Weight,
        )

        assertEquals(listOf(firstWeight, secondWeight), filteredRecords)
    }

    private fun record(id: String, type: CalendarRecordType): CalendarRecord =
        CalendarRecord(
            id = id,
            date = LocalDate.of(2026, 6, 21),
            type = type,
            note = "",
            weightKg = null,
            fetalMovementCount = null,
            fetalMovementPeriod = null,
            fetalMovementFeeling = null,
            exerciseMinutes = null,
            appointmentTime = null,
            appointmentLocation = null,
            appointmentDoctor = null,
            appointmentItems = null,
            appointmentResult = null,
            createdAtEpochMillis = 0L,
        )
}
