package com.yunqi.app.feature.settings

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.export.CalendarRecordCsvExporter
import com.yunqi.app.data.local.DailyReminderPreference
import com.yunqi.app.data.local.PregnancyProfileRepository
import com.yunqi.app.data.local.ReminderSettingsRepository
import com.yunqi.app.data.local.record.CalendarRecordRepository
import com.yunqi.app.domain.reminder.DailyReminderType
import com.yunqi.app.notification.AppointmentReminderScheduler
import com.yunqi.app.notification.DailyReminderScheduler
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val pregnancyProfileRepository = PregnancyProfileRepository(application.applicationContext)
    private val reminderSettingsRepository = ReminderSettingsRepository(application.applicationContext)
    private val calendarRecordRepository = CalendarRecordRepository(application.applicationContext)
    private val reminderScheduler = AppointmentReminderScheduler(application.applicationContext)
    private val dailyReminderScheduler = DailyReminderScheduler(application.applicationContext)
    private val csvExporter = CalendarRecordCsvExporter()

    val uiState: StateFlow<SettingsUiState> = reminderSettingsRepository
        .reminderSettingsFlow
        .map { settings ->
            SettingsUiState(
                appointmentRemindersEnabled = settings.appointmentRemindersEnabled,
                dailyReminders = settings.dailyReminders,
            )
        }
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

    fun setDailyReminderEnabled(type: DailyReminderType, enabled: Boolean, time: String) {
        viewModelScope.launch {
            reminderSettingsRepository.setDailyReminder(type, enabled, time)
            if (enabled) {
                dailyReminderScheduler.schedule(type, time)
            } else {
                dailyReminderScheduler.cancel(type)
            }
        }
    }

    fun setDailyReminderTime(type: DailyReminderType, time: String) {
        viewModelScope.launch {
            reminderSettingsRepository.setDailyReminder(type = type, enabled = true, time = time)
            dailyReminderScheduler.schedule(type, time)
        }
    }

    fun syncDailyReminders(reminders: List<DailyReminderPreference>) {
        viewModelScope.launch {
            dailyReminderScheduler.cancelLegacy()
            reminders.forEach { reminder ->
                if (reminder.enabled) {
                    dailyReminderScheduler.schedule(reminder.type, reminder.time)
                } else {
                    dailyReminderScheduler.cancel(reminder.type)
                }
            }
        }
    }

    fun clearAllLocalData() {
        viewModelScope.launch {
            reminderScheduler.cancelAll()
            dailyReminderScheduler.cancelAll()
            calendarRecordRepository.deleteAll()
            pregnancyProfileRepository.clearProfile()
            reminderSettingsRepository.clearSettings()
        }
    }

    suspend fun exportCalendarRecordsCsv(): Uri = withContext(Dispatchers.IO) {
        val context = getApplication<Application>().applicationContext
        val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val exportFile = File(exportsDir, "yunqi-calendar-records.csv")
        val csv = csvExporter.export(calendarRecordRepository.allRecordsSnapshot())
        exportFile.writeText(csv, Charsets.UTF_8)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            exportFile,
        )
    }
}

data class SettingsUiState(
    val appointmentRemindersEnabled: Boolean = false,
    val dailyReminders: List<DailyReminderPreference> = DailyReminderType.entries.map { type ->
        DailyReminderPreference(
            type = type,
            enabled = false,
            time = type.defaultTime,
        )
    },
)
