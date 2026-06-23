package com.yunqi.app.notification

import com.yunqi.app.data.local.DailyReminderPreference
import com.yunqi.app.domain.reminder.DailyReminderType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderRestoreReceiverTest {
    @Test
    fun `restore failure log message omits sensitive exception details`() {
        val error = IllegalStateException("City Hospital appointment with Dr Chen failed")

        val message = reminderRestoreFailureLogMessage(error)

        assertTrue(message.contains("IllegalStateException"))
        assertFalse(message.contains("City Hospital"))
        assertFalse(message.contains("Dr Chen"))
    }

    @Test
    fun `restore plan always cancels legacy daily reminder work`() {
        val plan = dailyReminderRestorePlan(
            reminders = listOf(
                DailyReminderPreference(
                    type = DailyReminderType.Weight,
                    enabled = false,
                    time = "09:00",
                ),
            ),
        )

        assertTrue(plan.cancelLegacyWork)
    }

    @Test
    fun `restore plan separates enabled and disabled daily reminders`() {
        val weightReminder = DailyReminderPreference(
            type = DailyReminderType.Weight,
            enabled = true,
            time = "08:30",
        )
        val waterReminder = DailyReminderPreference(
            type = DailyReminderType.Water,
            enabled = false,
            time = "10:00",
        )

        val plan = dailyReminderRestorePlan(listOf(weightReminder, waterReminder))

        assertEquals(listOf(weightReminder), plan.remindersToSchedule)
        assertEquals(listOf(DailyReminderType.Water), plan.reminderTypesToCancel)
    }
}
