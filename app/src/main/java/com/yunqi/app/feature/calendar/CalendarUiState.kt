package com.yunqi.app.feature.calendar

import com.yunqi.app.domain.calendar.CalendarRecord
import java.time.LocalDate

data class CalendarUiState(
    val selectedDate: LocalDate,
    val records: List<CalendarRecord>,
)

