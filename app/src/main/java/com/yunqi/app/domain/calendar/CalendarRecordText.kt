package com.yunqi.app.domain.calendar

const val MAX_CALENDAR_RECORD_TEXT_LENGTH = 500

/**
 * Keeps locally stored calendar text bounded for list rendering, reminders, and CSV export.
 */
internal fun String.toStoredCalendarRecordText(): String =
    trim().take(MAX_CALENDAR_RECORD_TEXT_LENGTH)

internal fun String?.toOptionalStoredCalendarRecordText(): String? =
    orEmpty().toStoredCalendarRecordText().takeIf(String::isNotBlank)
