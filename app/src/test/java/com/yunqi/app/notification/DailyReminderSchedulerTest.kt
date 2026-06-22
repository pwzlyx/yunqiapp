package com.yunqi.app.notification

import com.yunqi.app.domain.reminder.DailyReminderType
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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

    @Test
    fun `uses distinct work names for reminder types`() {
        assertNotEquals(
            dailyReminderWorkName(DailyReminderType.Weight),
            dailyReminderWorkName(DailyReminderType.FetalMovement),
        )
        assertNotEquals("daily-record-reminder", dailyReminderWorkName(DailyReminderType.Weight))
    }

    @Test
    fun `uses custom message for custom reminder notification`() {
        val content = DailyReminderType.Custom.notificationContent("Pack hospital bag")

        assertEquals("Pack hospital bag", content.bodyText)
    }

    @Test
    fun `falls back to default body when custom reminder message is blank`() {
        val content = DailyReminderType.Custom.notificationContent("   ")

        assertNull(content.bodyText)
    }

    @Test
    fun `ignores custom message for fixed reminder types`() {
        val content = DailyReminderType.Weight.notificationContent("Custom weight text")

        assertNull(content.bodyText)
    }
}
