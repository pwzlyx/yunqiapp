package com.yunqi.app.feature.calendar

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType

/**
 * Filters the selected day's records while preserving the repository sort order.
 */
internal fun filterCalendarRecords(
    records: List<CalendarRecord>,
    recordFilter: CalendarRecordType?,
): List<CalendarRecord> =
    recordFilter?.let { type -> records.filter { it.type == type } } ?: records
