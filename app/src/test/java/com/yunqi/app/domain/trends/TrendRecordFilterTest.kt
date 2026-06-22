package com.yunqi.app.domain.trends

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class TrendRecordFilterTest {
    @Test
    fun `last seven days keeps inclusive date window`() {
        val records = listOf(
            record(id = "old", date = LocalDate.of(2026, 6, 14)),
            record(id = "start", date = LocalDate.of(2026, 6, 15)),
            record(id = "today", date = LocalDate.of(2026, 6, 21)),
            record(id = "future", date = LocalDate.of(2026, 6, 22)),
        )

        val filtered = TrendRecordFilter.filter(
            records = records,
            range = TrendRange.Last7Days,
            today = LocalDate.of(2026, 6, 21),
        )

        assertEquals(listOf("start", "today"), filtered.map(CalendarRecord::id))
    }

    @Test
    fun `all range excludes future records`() {
        val records = listOf(
            record(id = "old", date = LocalDate.of(2026, 1, 1)),
            record(id = "today", date = LocalDate.of(2026, 6, 21)),
            record(id = "future", date = LocalDate.of(2026, 6, 22)),
        )

        val filtered = TrendRecordFilter.filter(
            records = records,
            range = TrendRange.All,
            today = LocalDate.of(2026, 6, 21),
        )

        assertEquals(listOf("old", "today"), filtered.map(CalendarRecord::id))
    }

    private fun record(id: String, date: LocalDate) = CalendarRecord(
        id = id,
        date = date,
        type = CalendarRecordType.Weight,
        note = "",
        weightKg = 55.0,
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
