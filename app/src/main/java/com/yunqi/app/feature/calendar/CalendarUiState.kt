package com.yunqi.app.feature.calendar

import com.yunqi.app.domain.calendar.CalendarRecord
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val selectedDate: LocalDate,
    val displayedMonth: YearMonth = YearMonth.from(selectedDate),
    val monthDays: List<CalendarMonthDay> = emptyList(),
    val records: List<CalendarRecord>,
)
