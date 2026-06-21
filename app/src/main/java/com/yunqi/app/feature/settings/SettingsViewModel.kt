package com.yunqi.app.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.local.PregnancyProfileRepository
import com.yunqi.app.data.local.ReminderSettingsRepository
import com.yunqi.app.data.local.record.CalendarRecordRepository
import com.yunqi.app.notification.AppointmentReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val pregnancyProfileRepository = PregnancyProfileRepository(application.applicationContext)
    private val reminderSettingsRepository = ReminderSettingsRepository(application.applicationContext)
    private val calendarRecordRepository = CalendarRecordRepository(application.applicationContext)
    private val reminderScheduler = AppointmentReminderScheduler(application.applicationContext)

    val uiState: StateFlow<SettingsUiState> = reminderSettingsRepository
        .appointmentRemindersEnabledFlow
        .map { enabled -> SettingsUiState(appointmentRemindersEnabled = enabled) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    /**
     * Persists reminder preference and keeps WorkManager jobs aligned with the setting.
     */
    fun setAppointmentRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            reminderSettingsRepository.setAppointmentRemindersEnabled(enabled)
            if (enabled) {
                calendarRecordRepository.futureAppointmentRecords(LocalDate.now())
                    .forEach(reminderScheduler::schedule)
            } else {
                calendarRecordRepository.futureAppointmentRecords(LocalDate.now())
                    .forEach { record -> reminderScheduler.cancel(record.id) }
                reminderScheduler.cancelAll()
            }
        }
    }

    fun clearAllLocalData() {
        viewModelScope.launch {
            reminderScheduler.cancelAll()
            calendarRecordRepository.deleteAll()
            pregnancyProfileRepository.clearProfile()
            reminderSettingsRepository.clearSettings()
        }
    }
}

data class SettingsUiState(
    val appointmentRemindersEnabled: Boolean = false,
)
