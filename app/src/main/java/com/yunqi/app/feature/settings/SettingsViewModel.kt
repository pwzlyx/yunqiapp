package com.yunqi.app.feature.settings

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.export.CalendarRecordExportStore
import com.yunqi.app.data.export.CalendarRecordCsvExporter
import com.yunqi.app.data.local.ContentStatusRepository
import com.yunqi.app.data.local.DailyReminderPreference
import com.yunqi.app.data.local.DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES
import com.yunqi.app.data.local.PregnancyProfileRepository
import com.yunqi.app.data.local.ReminderSettingsRepository
import com.yunqi.app.data.local.record.CalendarRecordRepository
import com.yunqi.app.domain.reminder.DailyReminderType
import com.yunqi.app.notification.AppointmentReminderScheduler
import com.yunqi.app.notification.DailyReminderScheduler
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val pregnancyProfileRepository = PregnancyProfileRepository(application.applicationContext)
    private val reminderSettingsRepository = ReminderSettingsRepository(application.applicationContext)
    private val contentStatusRepository = ContentStatusRepository(application.applicationContext)
    private val calendarRecordRepository = CalendarRecordRepository(application.applicationContext)
    private val reminderScheduler = AppointmentReminderScheduler(application.applicationContext)
    private val dailyReminderScheduler = DailyReminderScheduler(application.applicationContext)
    private val csvExporter = CalendarRecordCsvExporter()
    private val exportStore = CalendarRecordExportStore()

    val uiState: StateFlow<SettingsUiState> = reminderSettingsRepository
        .reminderSettingsFlow
        .map { settings ->
            SettingsUiState(
                appointmentRemindersEnabled = settings.appointmentRemindersEnabled,
                appointmentReminderLeadMinutes = settings.appointmentReminderLeadMinutes,
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
            val leadMinutes = reminderSettingsRepository.reminderSettingsFlow.first().appointmentReminderLeadMinutes
            if (enabled) {
                calendarRecordRepository.futureAppointmentRecords(LocalDate.now())
                    .forEach { record -> reminderScheduler.schedule(record, leadMinutes) }
            } else {
                calendarRecordRepository.futureAppointmentRecords(LocalDate.now())
                    .forEach { record -> reminderScheduler.cancel(record.id) }
                reminderScheduler.cancelAll()
            }
        }
    }

    fun setAppointmentReminderLeadMinutes(leadMinutes: Long) {
        viewModelScope.launch {
            reminderSettingsRepository.setAppointmentReminderLeadMinutes(leadMinutes)
            if (reminderSettingsRepository.appointmentRemindersEnabledFlow.first()) {
                reminderScheduler.cancelAll()
                calendarRecordRepository.futureAppointmentRecords(LocalDate.now())
                    .forEach { record -> reminderScheduler.schedule(record, leadMinutes) }
            }
        }
    }

    fun setDailyReminderEnabled(
        type: DailyReminderType,
        enabled: Boolean,
        time: String,
        customMessage: String = "",
    ) {
        viewModelScope.launch {
            reminderSettingsRepository.setDailyReminder(type, enabled, time)
            if (enabled) {
                dailyReminderScheduler.schedule(type, time, customMessage)
            } else {
                dailyReminderScheduler.cancel(type)
            }
        }
    }

    fun setDailyReminderTime(type: DailyReminderType, time: String, customMessage: String = "") {
        viewModelScope.launch {
            reminderSettingsRepository.setDailyReminder(type = type, enabled = true, time = time)
            dailyReminderScheduler.schedule(type, time, customMessage)
        }
    }

    fun setDailyReminderCustomMessage(
        type: DailyReminderType,
        message: String,
        enabled: Boolean,
        time: String,
    ) {
        viewModelScope.launch {
            reminderSettingsRepository.setDailyReminderCustomMessage(type, message)
            if (enabled) {
                dailyReminderScheduler.schedule(type, time, message)
            }
        }
    }

    fun syncDailyReminders(reminders: List<DailyReminderPreference>) {
        viewModelScope.launch {
            dailyReminderScheduler.cancelLegacy()
            reminders.forEach { reminder ->
                if (reminder.enabled) {
                    dailyReminderScheduler.schedule(
                        type = reminder.type,
                        time = reminder.time,
                        customMessage = reminder.customMessage,
                    )
                } else {
                    dailyReminderScheduler.cancel(reminder.type)
                }
            }
        }
    }

    fun syncAppointmentReminders(leadMinutes: Long) {
        viewModelScope.launch {
            reminderScheduler.cancelAll()
            calendarRecordRepository.futureAppointmentRecords(LocalDate.now())
                .forEach { record -> reminderScheduler.schedule(record, leadMinutes) }
        }
    }

    fun clearPregnancyProfileAndReminders() {
        viewModelScope.launch {
            reminderScheduler.cancelAll()
            dailyReminderScheduler.cancelAll()
            pregnancyProfileRepository.clearProfile()
            reminderSettingsRepository.clearSettings()
        }
    }

    fun clearAllLocalData() {
        viewModelScope.launch {
            reminderScheduler.cancelAll()
            dailyReminderScheduler.cancelAll()
            calendarRecordRepository.deleteAll()
            pregnancyProfileRepository.clearProfile()
            reminderSettingsRepository.clearSettings()
            contentStatusRepository.clearStatus()
            exportStore.clear(getApplication<Application>().applicationContext.cacheDir)
        }
    }

    suspend fun exportCalendarRecordsCsv(): Uri = withContext(Dispatchers.IO) {
        val context = getApplication<Application>().applicationContext
        val csv = csvExporter.export(calendarRecordRepository.allRecordsSnapshot())
        val exportFile = exportStore.write(context.cacheDir, csv)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            exportFile,
        )
    }
}

data class SettingsUiState(
    val appointmentRemindersEnabled: Boolean = false,
    val appointmentReminderLeadMinutes: Long = DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES,
    val dailyReminders: List<DailyReminderPreference> = DailyReminderType.entries.map { type ->
        DailyReminderPreference(
            type = type,
            enabled = false,
            time = type.defaultTime,
            customMessage = "",
        )
    },
)
