package com.yunqi.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    /**
     * Persists the user's local appointment reminder preference.
     */
    suspend fun setAppointmentRemindersEnabled(enabled: Boolean) {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences[Keys.appointmentRemindersEnabled] = enabled
        }
    }

    suspend fun clearSettings() {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object Keys {
        val appointmentRemindersEnabled = booleanPreferencesKey("appointment_reminders_enabled")
    }
}
