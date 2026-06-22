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
}
