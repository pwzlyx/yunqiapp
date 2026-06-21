package com.yunqi.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.pregnancyProfileDataStore by preferencesDataStore(
    name = "pregnancy_profile",
)

class PregnancyProfileRepository(
    private val context: Context,
) {
    /**
     * Emits the locally stored pregnancy profile, or null before the user completes setup.
     */
    val profileFlow: Flow<PregnancyProfile?> = context.pregnancyProfileDataStore.data.map { preferences ->
        val method = preferences[Keys.method]
            ?.let { runCatching { PregnancyCalculationMethod.valueOf(it) }.getOrNull() }
            ?: return@map null
        val setupDate = preferences[Keys.setupDate]?.let(LocalDate::parse)
            ?: return@map null

        PregnancyProfile(
            calculationMethod = method,
            lmpDate = preferences[Keys.lmpDate]?.let(LocalDate::parse),
            dueDate = preferences[Keys.dueDate]?.let(LocalDate::parse),
            conceptionDate = preferences[Keys.conceptionDate]?.let(LocalDate::parse),
            gestationalWeekAtSetup = preferences[Keys.gestationalWeekAtSetup],
            gestationalDayAtSetup = preferences[Keys.gestationalDayAtSetup],
            setupDate = setupDate,
        )
    }

    /**
     * Persists the pregnancy profile to app-local DataStore preferences.
     */
    suspend fun saveProfile(profile: PregnancyProfile) {
        context.pregnancyProfileDataStore.edit { preferences ->
            preferences[Keys.method] = profile.calculationMethod.name
            preferences[Keys.setupDate] = profile.setupDate.toString()
            putOrRemove(preferences, Keys.lmpDate, profile.lmpDate?.toString())
            putOrRemove(preferences, Keys.dueDate, profile.dueDate?.toString())
            putOrRemove(preferences, Keys.conceptionDate, profile.conceptionDate?.toString())
            putOrRemove(preferences, Keys.gestationalWeekAtSetup, profile.gestationalWeekAtSetup)
            putOrRemove(preferences, Keys.gestationalDayAtSetup, profile.gestationalDayAtSetup)
        }
    }

    suspend fun clearProfile() {
        context.pregnancyProfileDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun <T> putOrRemove(
        preferences: androidx.datastore.preferences.core.MutablePreferences,
        key: androidx.datastore.preferences.core.Preferences.Key<T>,
        value: T?,
    ) {
        if (value == null) {
            preferences.remove(key)
        } else {
            preferences[key] = value
        }
    }

    private object Keys {
        val method = stringPreferencesKey("method")
        val lmpDate = stringPreferencesKey("lmp_date")
        val dueDate = stringPreferencesKey("due_date")
        val conceptionDate = stringPreferencesKey("conception_date")
        val gestationalWeekAtSetup = intPreferencesKey("gestational_week_at_setup")
        val gestationalDayAtSetup = intPreferencesKey("gestational_day_at_setup")
        val setupDate = stringPreferencesKey("setup_date")
    }
}
