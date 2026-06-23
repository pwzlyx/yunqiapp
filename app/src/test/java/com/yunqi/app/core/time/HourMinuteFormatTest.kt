package com.yunqi.app.core.time

import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HourMinuteFormatTest {
    @Test
    fun `accepts zero padded hour minute values`() {
        assertTrue("00:00".isStrictHourMinute())
        assertTrue("09:30".isStrictHourMinute())
        assertTrue("23:59".isStrictHourMinute())
        assertEquals(LocalTime.of(9, 30), " 09:30 ".toStrictHourMinuteOrNull())
    }

    @Test
    fun `rejects non padded out of range or second precision values`() {
        assertFalse("9:30".isStrictHourMinute())
        assertFalse("24:00".isStrictHourMinute())
        assertFalse("23:60".isStrictHourMinute())
        assertFalse("09:30:00".isStrictHourMinute())
        assertFalse("morning".isStrictHourMinute())
        assertNull("09:30:00".toStrictHourMinuteOrNull())
    }
}
