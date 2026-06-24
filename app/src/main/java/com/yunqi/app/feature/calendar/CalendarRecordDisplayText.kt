package com.yunqi.app.feature.calendar

object CalendarRecordDisplayText {
    /**
     * Keeps legacy blank or oversized stored text from overwhelming calendar review cards.
     */
    fun visibleOrNull(value: String?): String? =
        value
            ?.trim()
            ?.take(MAX_CALENDAR_RECORD_DISPLAY_TEXT_LENGTH)
            ?.takeIf(String::isNotBlank)
}

internal const val MAX_CALENDAR_RECORD_DISPLAY_TEXT_LENGTH = 240
