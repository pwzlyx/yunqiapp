package com.yunqi.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yunqi.app.domain.reminder.DailyReminderType
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
            val legacyDailyReminderEnabled = preferences[Keys.legacyDailyReminderEnabled] ?: false
            val legacyDailyReminderTime = preferences[Keys.legacyDailyReminderTime] ?: DEFAULT_DAILY_REMINDER_TIME
            ReminderSettings(
                appointmentRemindersEnabled = preferences[Keys.appointmentRemindersEnabled] ?: false,
                dailyReminders = DailyReminderType.entries.map { type ->
                    DailyReminderPreference(
                        type = type,
                        enabled = preferences[Keys.dailyReminderEnabled(type)]
                            ?: type.usesLegacyDailyReminder(legacyDailyReminderEnabled),
                        time = preferences[Keys.dailyReminderTime(type)]
                            ?: type.legacyOrDefaultTime(legacyDailyReminderEnabled, legacyDailyReminderTime),
                    )
                },
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

    suspend fun setDailyReminder(type: DailyReminderType, enabled: Boolean, time: String) {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences[Keys.dailyReminderEnabled(type)] = enabled
            preferences[Keys.dailyReminderTime(type)] = time
        }
    }

    suspend fun clearSettings() {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object Keys {
        val appointmentRemindersEnabled = booleanPreferencesKey("appointment_reminders_enabled")
        val legacyDailyReminderEnabled = booleanPreferencesKey("daily_reminder_enabled")
        val legacyDailyReminderTime = stringPreferencesKey("daily_reminder_time")

        fun dailyReminderEnabled(type: DailyReminderType) =
            booleanPreferencesKey("daily_reminder_${type.name.lowercase()}_enabled")

        fun dailyReminderTime(type: DailyReminderType) =
            stringPreferencesKey("daily_reminder_${type.name.lowercase()}_time")
    }
}

data class ReminderSettings(
    val appointmentRemindersEnabled: Boolean,
    val dailyReminders: List<DailyReminderPreference>,
)

data class DailyReminderPreference(
    val type: DailyReminderType,
    val enabled: Boolean,
    val time: String,
)

private fun DailyReminderType.usesLegacyDailyReminder(legacyEnabled: Boolean): Boolean =
    legacyEnabled && this in legacyReminderTypes

private fun DailyReminderType.legacyOrDefaultTime(legacyEnabled: Boolean, legacyTime: String): String =
    if (usesLegacyDailyReminder(legacyEnabled)) legacyTime else defaultTime

private val legacyReminderTypes = setOf(
    DailyReminderType.Weight,
    DailyReminderType.FetalMovement,
    DailyReminderType.Exercise,
)
