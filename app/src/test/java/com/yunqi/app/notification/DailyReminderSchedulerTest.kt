package com.yunqi.app.notification

import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DailyReminderSchedulerTest {
    @Test
    fun `calculates delay for later reminder today`() {
        val delayMillis = calculateDailyReminderInitialDelay(
            time = "13:00",
            now = LocalDateTime.of(2026, 6, 21, 9, 0),
        )

        assertEquals(4L * 60L * 60L * 1000L, delayMillis)
    }

    @Test
    fun `rolls reminder to tomorrow when time has passed`() {
        val delayMillis = calculateDailyReminderInitialDelay(
            time = "09:00",
            now = LocalDateTime.of(2026, 6, 21, 10, 0),
        )

        assertEquals(23L * 60L * 60L * 1000L, delayMillis)
    }

    @Test
    fun `ignores invalid reminder time`() {
        val delayMillis = calculateDailyReminderInitialDelay(
            time = "morning",
            now = LocalDateTime.of(2026, 6, 21, 10, 0),
        )

        assertNull(delayMillis)
    }
}
