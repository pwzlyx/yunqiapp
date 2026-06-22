package com.yunqi.app.feature.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.local.ReminderSettingsRepository
import com.yunqi.app.data.local.record.CalendarRecordRepository
import com.yunqi.app.domain.calendar.CalendarRecordType
import com.yunqi.app.notification.AppointmentReminderScheduler
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CalendarRecordRepository(application.applicationContext)
    private val reminderSettingsRepository = ReminderSettingsRepository(application.applicationContext)
    private val reminderScheduler = AppointmentReminderScheduler(application.applicationContext)
    private val monthBuilder = CalendarMonthBuilder()
    private val parser = CalendarRecordFormParser()
    private val selectedDate = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CalendarUiState> = selectedDate
        .flatMapLatest { date ->
            val displayedMonth = YearMonth.from(date)
            combine(
                repository.recordsForDate(date),
                repository.recordsBetween(
                    startDate = displayedMonth.atDay(1),
                    endDate = displayedMonth.atEndOfMonth(),
                ),
            ) { records, monthRecords ->
                CalendarUiState(
                    selectedDate = date,
                    displayedMonth = displayedMonth,
                    monthDays = monthBuilder.build(displayedMonth, monthRecords),
                    records = records,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CalendarUiState(
                selectedDate = LocalDate.now(),
                records = emptyList(),
            ),
        )

    /**
     * Updates the selected date when the user enters a valid ISO date.
     */
    fun selectDate(rawDate: String): CalendarRecordActionResult {
        val date = runCatching { LocalDate.parse(rawDate.trim()) }.getOrNull()
            ?: return CalendarRecordActionResult.InvalidDate
        selectedDate.value = date
        return CalendarRecordActionResult.Success
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    suspend fun save(input: CalendarRecordInput): CalendarRecordActionResult = when (val result = parser.parse(input)) {
        is CalendarRecordParseResult.Success -> {
            repository.save(result.record)
            val reminderSettings = reminderSettingsRepository.reminderSettingsFlow.first()
            if (result.record.type == CalendarRecordType.Appointment && reminderSettings.appointmentRemindersEnabled) {
                reminderScheduler.schedule(result.record, reminderSettings.appointmentReminderLeadMinutes)
            } else {
                reminderScheduler.cancel(result.record.id)
            }
            selectedDate.value = result.record.date
            CalendarRecordActionResult.Success
        }

        CalendarRecordParseResult.InvalidDate -> CalendarRecordActionResult.InvalidDate
        CalendarRecordParseResult.InvalidWeight -> CalendarRecordActionResult.InvalidWeight
        CalendarRecordParseResult.InvalidFetalMovement -> CalendarRecordActionResult.InvalidFetalMovement
        CalendarRecordParseResult.InvalidExerciseMinutes -> CalendarRecordActionResult.InvalidExerciseMinutes
        CalendarRecordParseResult.InvalidAppointmentTime -> CalendarRecordActionResult.InvalidAppointmentTime
        CalendarRecordParseResult.InvalidRecordContent -> CalendarRecordActionResult.InvalidRecordContent
    }

    suspend fun delete(id: String) {
        repository.delete(id)
        reminderScheduler.cancel(id)
    }
}

sealed interface CalendarRecordActionResult {
    data object Success : CalendarRecordActionResult
    data object InvalidDate : CalendarRecordActionResult
    data object InvalidWeight : CalendarRecordActionResult
    data object InvalidFetalMovement : CalendarRecordActionResult
    data object InvalidExerciseMinutes : CalendarRecordActionResult
    data object InvalidAppointmentTime : CalendarRecordActionResult
    data object InvalidRecordContent : CalendarRecordActionResult
}
