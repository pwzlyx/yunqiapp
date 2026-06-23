package com.yunqi.app.data.local

import com.yunqi.app.domain.reminder.DailyReminderType
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderSettingsRepositoryTest {
    @Test
    fun `keeps valid daily reminder time and trims whitespace`() {
        assertEquals(
            "20:00",
            " 20:00 ".toValidDailyReminderTimeOrDefault(DailyReminderType.Custom),
        )
    }

    @Test
    fun `falls back to type default time when stored daily reminder time is invalid`() {
        assertEquals(
            DailyReminderType.Water.defaultTime,
            "noon".toValidDailyReminderTimeOrDefault(DailyReminderType.Water),
        )
    }

    @Test
    fun `uses valid legacy time for legacy daily reminder types`() {
        assertEquals(
            "18:30",
            null.toValidDailyReminderTimeOrDefault(
                type = DailyReminderType.Weight,
                legacyEnabled = true,
                legacyTime = " 18:30 ",
            ),
        )
    }

    @Test
    fun `falls back to type default time when legacy daily reminder time is invalid`() {
        assertEquals(
            DailyReminderType.Exercise.defaultTime,
            null.toValidDailyReminderTimeOrDefault(
                type = DailyReminderType.Exercise,
                legacyEnabled = true,
                legacyTime = "after work",
            ),
        )
    }

    @Test
    fun `uses type default time when no stored or legacy daily reminder time exists`() {
        assertEquals(
            DailyReminderType.FetalMovement.defaultTime,
            null.toValidDailyReminderTimeOrDefault(DailyReminderType.FetalMovement),
        )
    }
}
