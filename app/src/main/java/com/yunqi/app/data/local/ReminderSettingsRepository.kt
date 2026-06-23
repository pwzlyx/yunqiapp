package com.yunqi.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yunqi.app.domain.reminder.DailyReminderType
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val DEFAULT_DAILY_REMINDER_TIME = "09:00"
const val DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES = 60L

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
                appointmentReminderLeadMinutes = preferences[Keys.appointmentReminderLeadMinutes]
                    ?: DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES,
                dailyReminders = DailyReminderType.entries.map { type ->
                    DailyReminderPreference(
                        type = type,
                        enabled = preferences[Keys.dailyReminderEnabled(type)]
                            ?: type.usesLegacyDailyReminder(legacyDailyReminderEnabled),
                        time = preferences[Keys.dailyReminderTime(type)]
                            .toValidDailyReminderTimeOrDefault(
                                type = type,
                                legacyEnabled = legacyDailyReminderEnabled,
                                legacyTime = legacyDailyReminderTime,
                            ),
                        customMessage = preferences[Keys.dailyReminderCustomMessage(type)].orEmpty(),
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

    suspend fun setAppointmentReminderLeadMinutes(leadMinutes: Long) {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences[Keys.appointmentReminderLeadMinutes] = leadMinutes
        }
    }

    suspend fun setDailyReminder(type: DailyReminderType, enabled: Boolean, time: String) {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences[Keys.dailyReminderEnabled(type)] = enabled
            preferences[Keys.dailyReminderTime(type)] = time.toValidDailyReminderTimeOrDefault(type)
        }
    }

    suspend fun setDailyReminderCustomMessage(type: DailyReminderType, message: String) {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences[Keys.dailyReminderCustomMessage(type)] = message.trim()
        }
    }

    suspend fun clearSettings() {
        context.reminderSettingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private object Keys {
        val appointmentRemindersEnabled = booleanPreferencesKey("appointment_reminders_enabled")
        val appointmentReminderLeadMinutes = longPreferencesKey("appointment_reminder_lead_minutes")
        val legacyDailyReminderEnabled = booleanPreferencesKey("daily_reminder_enabled")
        val legacyDailyReminderTime = stringPreferencesKey("daily_reminder_time")

        fun dailyReminderEnabled(type: DailyReminderType) =
            booleanPreferencesKey("daily_reminder_${type.name.lowercase()}_enabled")

        fun dailyReminderTime(type: DailyReminderType) =
            stringPreferencesKey("daily_reminder_${type.name.lowercase()}_time")

        fun dailyReminderCustomMessage(type: DailyReminderType) =
            stringPreferencesKey("daily_reminder_${type.name.lowercase()}_custom_message")
    }
}

data class ReminderSettings(
    val appointmentRemindersEnabled: Boolean,
    val appointmentReminderLeadMinutes: Long = DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES,
    val dailyReminders: List<DailyReminderPreference>,
)

data class DailyReminderPreference(
    val type: DailyReminderType,
    val enabled: Boolean,
    val time: String,
    val customMessage: String = "",
)

private fun DailyReminderType.usesLegacyDailyReminder(legacyEnabled: Boolean): Boolean =
    legacyEnabled && this in legacyReminderTypes

private fun DailyReminderType.legacyOrDefaultTime(legacyEnabled: Boolean, legacyTime: String): String =
    if (usesLegacyDailyReminder(legacyEnabled)) legacyTime else defaultTime

internal fun String?.toValidDailyReminderTimeOrDefault(
    type: DailyReminderType,
    legacyEnabled: Boolean = false,
    legacyTime: String = DEFAULT_DAILY_REMINDER_TIME,
): String {
    val candidate = this ?: type.legacyOrDefaultTime(legacyEnabled, legacyTime)
    val trimmed = candidate.trim()
    return if (trimmed.isValidHourMinute()) trimmed else type.defaultTime
}

private fun String.isValidHourMinute(): Boolean =
    runCatching { LocalTime.parse(this) }.isSuccess

private val legacyReminderTypes = setOf(
    DailyReminderType.Weight,
    DailyReminderType.FetalMovement,
    DailyReminderType.Exercise,
)
