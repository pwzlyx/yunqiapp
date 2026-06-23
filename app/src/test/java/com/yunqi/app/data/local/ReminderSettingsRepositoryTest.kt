package com.yunqi.app.data.local

import com.yunqi.app.domain.reminder.DailyReminderType
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderSettingsRepositoryTest {
    @Test
    fun `keeps supported appointment reminder lead minutes`() {
        assertEquals(60L, 60L.toSupportedAppointmentReminderLeadMinutes())
        assertEquals(360L, 360L.toSupportedAppointmentReminderLeadMinutes())
        assertEquals(1440L, 1440L.toSupportedAppointmentReminderLeadMinutes())
    }

    @Test
    fun `falls back to default appointment reminder lead minutes for unsupported values`() {
        assertEquals(DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES, null.toSupportedAppointmentReminderLeadMinutes())
        assertEquals(DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES, 0L.toSupportedAppointmentReminderLeadMinutes())
        assertEquals(DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES, (-1L).toSupportedAppointmentReminderLeadMinutes())
        assertEquals(DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES, 99999L.toSupportedAppointmentReminderLeadMinutes())
    }

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
