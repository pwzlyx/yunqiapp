package com.yunqi.app.feature.calendar

object CalendarRecordDisplayText {
    /**
     * Keeps legacy blank or whitespace-only stored text out of calendar review cards.
     */
    fun visibleOrNull(value: String?): String? =
        value?.trim()?.takeIf(String::isNotBlank)
}
