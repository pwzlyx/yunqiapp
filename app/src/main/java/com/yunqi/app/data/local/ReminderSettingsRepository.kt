package com.yunqi.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val DEFAULT_DAILY_REMINDER_TIME = "09:00"

private val Context.reminderSettingsDataStore by preferencesDataStore(
    name = "reminder_settings",
)

class ReminderSettingsRepository(
    private val context: Context,
) {
    /**
     * Emits whether appointment reminders should be scheduled for local calendar records.
     */
    val appointmentRemindersEnabledFlow: Flow<Boolean> =
        context.reminderSettingsDataStore.data.map { preferences ->
            preferences[Keys.appointmentRemindersEnabled] ?: false
        }

    val reminderSettingsFlow: Flow<ReminderSettings> =
        context.reminderSettingsDataStore.data.map { preferences ->
            ReminderSettings(
                appointmentRemindersEnabled = preferences[Keys.appointmentRemindersEnabled] ?: false,
                dailyReminderEnabled = preferences[Keys.dailyReminderEnabled] ?: false,
                dailyReminderTime = preferences[Keys.dailyReminderTime] ?: DEFAULT_DAILY_REMINDER_TIME,
            )
        }

    /**
     * Persists the user's local appointment reminder preference.
     */
    suspend fun setAppointmentRemindersEnabled(enabled: Boolean) {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences[Keys.appointmentRemindersEnabled] = enabled
        }
    }

    suspend fun setDailyReminder(enabled: Boolean, time: String) {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences[Keys.dailyReminderEnabled] = enabled
            preferences[Keys.dailyReminderTime] = time
        }
    }

    suspend fun clearSettings() {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object Keys {
        val appointmentRemindersEnabled = booleanPreferencesKey("appointment_reminders_enabled")
        val dailyReminderEnabled = booleanPreferencesKey("daily_reminder_enabled")
        val dailyReminderTime = androidx.datastore.preferences.core.stringPreferencesKey("daily_reminder_time")
    }
}

data class ReminderSettings(
    val appointmentRemindersEnabled: Boolean,
    val dailyReminderEnabled: Boolean,
    val dailyReminderTime: String,
)
