package com.yunqi.app.feature.calendar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalendarRecordDisplayTextTest {
    @Test
    fun `hides null blank and whitespace only text`() {
        assertNull(CalendarRecordDisplayText.visibleOrNull(null))
        assertNull(CalendarRecordDisplayText.visibleOrNull(""))
        assertNull(CalendarRecordDisplayText.visibleOrNull("   "))
    }

    @Test
    fun `trims visible calendar record text`() {
        assertEquals(
            "City Hospital",
            CalendarRecordDisplayText.visibleOrNull("  City Hospital  "),
        )
    }
}
