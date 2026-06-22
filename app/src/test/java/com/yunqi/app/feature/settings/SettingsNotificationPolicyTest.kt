package com.yunqi.app.feature.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsNotificationPolicyTest {
    @Test
    fun `preserves reminder preferences when notifications are unavailable`() {
        assertEquals(
            NotificationAvailabilityAction.PreserveReminderPreferences,
            notificationAvailabilityAction(notificationsAllowed = false),
        )
    }

    @Test
    fun `syncs scheduled reminder work when notifications are available`() {
        assertEquals(
            NotificationAvailabilityAction.SyncScheduledReminderWork,
            notificationAvailabilityAction(notificationsAllowed = true),
        )
    }

    @Test
    fun `preserves appointment reminder preference when notifications are unavailable`() {
        assertEquals(
            AppointmentReminderAvailabilityAction.PreserveAppointmentReminderPreference,
            appointmentReminderAvailabilityAction(
                notificationsAllowed = false,
                appointmentRemindersEnabled = true,
            ),
        )
    }

    @Test
    fun `preserves appointment reminder preference when appointment reminders are disabled`() {
        assertEquals(
            AppointmentReminderAvailabilityAction.PreserveAppointmentReminderPreference,
            appointmentReminderAvailabilityAction(
                notificationsAllowed = true,
                appointmentRemindersEnabled = false,
            ),
        )
    }

    @Test
    fun `syncs appointment reminder work when notifications and appointment reminders are enabled`() {
        assertEquals(
            AppointmentReminderAvailabilityAction.SyncScheduledAppointmentWork,
            appointmentReminderAvailabilityAction(
                notificationsAllowed = true,
                appointmentRemindersEnabled = true,
            ),
        )
    }
}
