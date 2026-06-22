package com.yunqi.app.notification

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
}
