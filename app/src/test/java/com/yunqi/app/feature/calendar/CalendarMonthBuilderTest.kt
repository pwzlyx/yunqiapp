package com.yunqi.app.feature.calendar

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarMonthBuilderTest {
    private val builder = CalendarMonthBuilder()

    @Test
    fun `builds six week grid starting on monday`() {
        val days = builder.build(
            displayedMonth = YearMonth.of(2026, 6),
            records = emptyList(),
        )

        assertEquals(42, days.size)
        assertEquals(LocalDate.of(2026, 6, 1), days.first().date)
        assertEquals(LocalDate.of(2026, 7, 12), days.last().date)
        assertTrue(days.first().isInDisplayedMonth)
        assertFalse(days.last().isInDisplayedMonth)
    }

    @Test
    fun `includes leading days from previous month`() {
        val days = builder.build(
            displayedMonth = YearMonth.of(2026, 8),
            records = emptyList(),
        )

        assertEquals(LocalDate.of(2026, 7, 27), days.first().date)
        assertFalse(days.first().isInDisplayedMonth)
    }

    @Test
    fun `attaches record type markers to matching dates`() {
        val days = builder.build(
            displayedMonth = YearMonth.of(2026, 6),
            records = listOf(
                record(LocalDate.of(2026, 6, 21), CalendarRecordType.Weight),
                record(LocalDate.of(2026, 6, 21), CalendarRecordType.FetalMovement),
            ),
        )

        val selectedDay = days.first { it.date == LocalDate.of(2026, 6, 21) }
        assertEquals(
            setOf(CalendarRecordType.Weight, CalendarRecordType.FetalMovement),
            selectedDay.recordTypes,
        )
    }

    private fun record(date: LocalDate, type: CalendarRecordType) = CalendarRecord(
        id = "$date-$type",
        date = date,
        type = type,
        note = "",
        weightKg = null,
        fetalMovementCount = null,
        appointmentTime = null,
        appointmentLocation = null,
        createdAtEpochMillis = 0L,
    )
}
