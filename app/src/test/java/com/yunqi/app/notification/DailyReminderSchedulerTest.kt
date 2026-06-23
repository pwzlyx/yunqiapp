package com.yunqi.app.notification

import com.yunqi.app.domain.reminder.DailyReminderType
import com.yunqi.app.domain.reminder.MAX_DAILY_REMINDER_CUSTOM_MESSAGE_LENGTH
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
    fun `ignores reminder time with seconds`() {
        val delayMillis = calculateDailyReminderInitialDelay(
            time = "13:00:00",
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
    fun `chooses schedule decision for valid daily reminder time`() {
        val decision = dailyReminderScheduleDecision(
            type = DailyReminderType.Custom,
            time = "20:00",
            customMessage = " Pack hospital bag ",
            now = LocalDateTime.of(2026, 6, 21, 19, 0),
        )

        assertEquals(
            DailyReminderScheduleDecision.Schedule(
                type = DailyReminderType.Custom,
                delayMillis = 60L * 60L * 1000L,
                customMessage = "Pack hospital bag",
            ),
            decision,
        )
    }

    @Test
    fun `truncates long custom message before scheduling work data`() {
        val decision = dailyReminderScheduleDecision(
            type = DailyReminderType.Custom,
            time = "20:00",
            customMessage = "x".repeat(MAX_DAILY_REMINDER_CUSTOM_MESSAGE_LENGTH + 1),
            now = LocalDateTime.of(2026, 6, 21, 19, 0),
        )

        assertEquals(
            "x".repeat(MAX_DAILY_REMINDER_CUSTOM_MESSAGE_LENGTH),
            (decision as DailyReminderScheduleDecision.Schedule).customMessage,
        )
    }

    @Test
    fun `chooses cancel decision for invalid daily reminder time`() {
        val decision = dailyReminderScheduleDecision(
            type = DailyReminderType.Water,
            time = "noon",
            customMessage = "",
            now = LocalDateTime.of(2026, 6, 21, 10, 0),
        )

        assertEquals(DailyReminderScheduleDecision.Cancel(DailyReminderType.Water), decision)
    }

    @Test
    fun `uses custom message for custom reminder notification`() {
        val content = DailyReminderType.Custom.notificationContent("Pack hospital bag")

        assertEquals("Pack hospital bag", content.bodyText)
    }

    @Test
    fun `truncates long custom reminder notification body`() {
        val content = DailyReminderType.Custom.notificationContent(
            "x".repeat(MAX_DAILY_REMINDER_CUSTOM_MESSAGE_LENGTH + 1),
        )

        assertEquals(
            "x".repeat(MAX_DAILY_REMINDER_CUSTOM_MESSAGE_LENGTH),
            content.bodyText,
        )
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
