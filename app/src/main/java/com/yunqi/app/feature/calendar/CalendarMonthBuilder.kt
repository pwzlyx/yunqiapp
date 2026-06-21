package com.yunqi.app.feature.calendar

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private const val CALENDAR_GRID_DAYS = 42

class CalendarMonthBuilder {
    /**
     * Builds a stable six-week month grid and attaches record type markers to matching dates.
     */
    fun build(displayedMonth: YearMonth, records: List<CalendarRecord>): List<CalendarMonthDay> {
        val firstDayOfMonth = displayedMonth.atDay(1)
        val leadingDays = firstDayOfMonth.daysSinceWeekStart()
        val firstGridDate = firstDayOfMonth.minusDays(leadingDays.toLong())
        val recordTypesByDate = records
            .groupBy(CalendarRecord::date)
            .mapValues { entry -> entry.value.map(CalendarRecord::type).toSet() }

        return (0 until CALENDAR_GRID_DAYS).map { offset ->
            val date = firstGridDate.plusDays(offset.toLong())
            CalendarMonthDay(
                date = date,
                isInDisplayedMonth = YearMonth.from(date) == displayedMonth,
                recordTypes = recordTypesByDate[date].orEmpty(),
            )
        }
    }

    private fun LocalDate.daysSinceWeekStart(): Int =
        (dayOfWeek.value - DayOfWeek.MONDAY.value + DayOfWeek.entries.size) % DayOfWeek.entries.size
}

data class CalendarMonthDay(
    val date: LocalDate,
    val isInDisplayedMonth: Boolean,
    val recordTypes: Set<CalendarRecordType>,
)
