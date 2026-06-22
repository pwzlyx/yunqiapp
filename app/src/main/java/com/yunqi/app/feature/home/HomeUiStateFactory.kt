package com.yunqi.app.feature.home

import com.yunqi.app.R
import com.yunqi.app.data.content.PregnancyContentRepository
import com.yunqi.app.data.local.ContentStatus
import com.yunqi.app.data.local.DailyReminderPreference
import com.yunqi.app.data.local.DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES
import com.yunqi.app.data.local.ReminderSettings
import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import com.yunqi.app.domain.pregnancy.calculateProgress
import com.yunqi.app.domain.reminder.DailyReminderType
import java.time.LocalDate

class HomeUiStateFactory(
    private val contentRepository: PregnancyContentRepository = PregnancyContentRepository(),
    private val todayProvider: () -> LocalDate = LocalDate::now,
) {
    /**
     * Builds render-ready home state from the local pregnancy profile.
     */
    fun create(
        profile: PregnancyProfile?,
        contentStatus: ContentStatus = ContentStatus(),
        reminderSettings: ReminderSettings = ReminderSettings(
            appointmentRemindersEnabled = false,
            appointmentReminderLeadMinutes = DEFAULT_APPOINTMENT_REMINDER_LEAD_MINUTES,
            dailyReminders = emptyList(),
        ),
        calendarRecords: List<CalendarRecord> = emptyList(),
    ): HomeUiState {
        if (profile == null) return HomeUiState.ProfileMissing

        val today = todayProvider()
        val progress = profile.calculateProgress(today = today)
        return HomeUiState.Ready(
            progress = progress,
            calculationMethod = profile.calculationMethod,
            exerciseRestricted = profile.exerciseRestricted,
            contentCards = contentRepository.cardsForWeek(progress.week),
            reminderItems = buildReminderItems(
                reminderSettings = reminderSettings,
                calendarRecords = calendarRecords,
                today = today,
            ),
            contentStatus = contentStatus,
        )
    }

    private fun buildReminderItems(
        reminderSettings: ReminderSettings,
        calendarRecords: List<CalendarRecord>,
        today: LocalDate,
    ): List<HomeReminderItem> {
        val appointmentItems = calendarRecords
            .filter { it.date == today && it.type == CalendarRecordType.Appointment }
            .sortedWith(compareBy<CalendarRecord> { it.appointmentTime.orEmpty() }.thenBy { it.createdAtEpochMillis })
            .map { record ->
                HomeReminderItem(
                    id = "appointment_${record.id}",
                    titleResId = R.string.home_today_appointment_reminder,
                    detail = listOfNotNull(record.appointmentTime, record.appointmentLocation)
                        .filter(String::isNotBlank)
                        .joinToString(" - ")
                        .ifBlank { null },
                    actionRecordType = CalendarRecordType.Appointment,
                )
            }

        val dailyItems = reminderSettings.dailyReminders
            .filter { it.enabled }
            .map { reminder ->
                HomeReminderItem(
                    id = "daily_${reminder.type.name}",
                    titleResId = reminder.type.homeTitleResId(),
                    detail = reminder.homeDetail(),
                    actionRecordType = reminder.type.actionRecordType(),
                )
            }

        return appointmentItems + dailyItems
    }
}

private fun DailyReminderPreference.homeDetail(): String =
    if (type == DailyReminderType.Custom && customMessage.isNotBlank()) {
        listOf(time, customMessage.trim()).joinToString(" - ")
    } else {
        time
    }

private fun DailyReminderType.homeTitleResId(): Int = when (this) {
    DailyReminderType.Weight -> R.string.settings_daily_reminder_weight
    DailyReminderType.FetalMovement -> R.string.settings_daily_reminder_fetal_movement
    DailyReminderType.Vitamin -> R.string.settings_daily_reminder_vitamin
    DailyReminderType.Water -> R.string.settings_daily_reminder_water
    DailyReminderType.Exercise -> R.string.settings_daily_reminder_exercise
    DailyReminderType.Custom -> R.string.settings_daily_reminder_custom
}

private fun DailyReminderType.actionRecordType(): CalendarRecordType? = when (this) {
    DailyReminderType.Weight -> CalendarRecordType.Weight
    DailyReminderType.FetalMovement -> CalendarRecordType.FetalMovement
    DailyReminderType.Exercise -> CalendarRecordType.Exercise
    DailyReminderType.Vitamin,
    DailyReminderType.Water,
    DailyReminderType.Custom -> null
}
