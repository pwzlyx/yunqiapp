package com.yunqi.app.feature.calendar

import com.yunqi.app.core.time.isStrictHourMinute
import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType

private const val LAST_APPOINTMENT_SORT_TIME = "99:99"

object CalendarRecordListSorter {
    /**
     * Orders a selected day's records for review: timed appointments first, then untimed appointments,
     * then the remaining records with the newest local entry first.
     */
    fun sort(records: List<CalendarRecord>): List<CalendarRecord> =
        records.sortedWith(
            compareBy<CalendarRecord> { it.calendarSortBucket() }
                .thenBy { it.appointmentSortTime() ?: LAST_APPOINTMENT_SORT_TIME }
                .thenByDescending { it.createdAtEpochMillis }
                .thenBy { it.id },
        )
}

private fun CalendarRecord.calendarSortBucket(): Int = when {
    type == CalendarRecordType.Appointment && appointmentSortTime() != null -> 0
    type == CalendarRecordType.Appointment -> 1
    else -> 2
}

private fun CalendarRecord.appointmentSortTime(): String? =
    appointmentTime?.trim()?.takeIf(String::isStrictHourMinute)
