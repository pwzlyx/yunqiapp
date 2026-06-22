package com.yunqi.app.feature.home

import com.yunqi.app.R
import com.yunqi.app.data.content.PregnancyContentCategory
import com.yunqi.app.data.local.ContentStatus
import com.yunqi.app.data.local.DailyReminderPreference
import com.yunqi.app.data.local.ReminderSettings
import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import com.yunqi.app.domain.reminder.DailyReminderType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeUiStateFactoryTest {
    @Test
    fun `returns missing state before profile setup`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )

        assertEquals(HomeUiState.ProfileMissing, factory.create(null))
    }

    @Test
    fun `builds ready state with pregnancy progress and content cards`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )
        val profile = PregnancyProfile(
            calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
            lmpDate = LocalDate.of(2026, 3, 1),
            dueDate = null,
            conceptionDate = null,
            gestationalWeekAtSetup = null,
            gestationalDayAtSetup = null,
            setupDate = LocalDate.of(2026, 6, 1),
        )

        val state = factory.create(profile) as HomeUiState.Ready

        assertEquals(16, state.progress.week)
        assertEquals(0, state.progress.day)
        assertEquals(PregnancyCalculationMethod.LastMenstrualPeriod, state.calculationMethod)
        assertEquals(false, state.exerciseRestricted)
        assertTrue(state.contentCards.any { it.category == PregnancyContentCategory.Diet })
        assertTrue(state.contentCards.any { it.category == PregnancyContentCategory.Exercise })
        assertTrue(state.contentCards.any { it.category == PregnancyContentCategory.AntenatalCare })
        assertTrue(state.contentCards.any { it.category == PregnancyContentCategory.Safety })
    }

    @Test
    fun `ready state exposes exercise restriction`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )
        val profile = PregnancyProfile(
            calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
            lmpDate = LocalDate.of(2026, 3, 1),
            dueDate = null,
            conceptionDate = null,
            gestationalWeekAtSetup = null,
            gestationalDayAtSetup = null,
            exerciseRestricted = true,
            setupDate = LocalDate.of(2026, 6, 1),
        )

        val state = factory.create(profile) as HomeUiState.Ready

        assertEquals(true, state.exerciseRestricted)
    }

    @Test
    fun `ready state includes local content status`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )
        val profile = PregnancyProfile(
            calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
            lmpDate = LocalDate.of(2026, 3, 1),
            dueDate = null,
            conceptionDate = null,
            gestationalWeekAtSetup = null,
            gestationalDayAtSetup = null,
            setupDate = LocalDate.of(2026, 6, 1),
        )
        val contentStatus = ContentStatus(
            readContentIds = setOf("diet_second_trimester_fish"),
            favoriteContentIds = setOf("diet_second_trimester_fish"),
            hiddenContentIds = setOf("exercise_second_trimester_routine"),
        )

        val state = factory.create(profile, contentStatus) as HomeUiState.Ready

        assertEquals(contentStatus, state.contentStatus)
    }

    @Test
    fun `ready state includes enabled daily reminders and today's appointments`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )
        val profile = pregnancyProfile()
        val reminderSettings = ReminderSettings(
            appointmentRemindersEnabled = true,
            dailyReminders = listOf(
                DailyReminderPreference(DailyReminderType.Weight, enabled = true, time = "09:00"),
                DailyReminderPreference(DailyReminderType.Water, enabled = true, time = "13:00"),
                DailyReminderPreference(DailyReminderType.Exercise, enabled = false, time = "17:00"),
            ),
        )
        val records = listOf(
            appointmentRecord(
                id = "today",
                date = LocalDate.of(2026, 6, 21),
                time = "10:30",
                location = "City Hospital",
            ),
            appointmentRecord(
                id = "tomorrow",
                date = LocalDate.of(2026, 6, 22),
                time = "09:00",
                location = "Other Hospital",
            ),
        )

        val state = factory.create(
            profile = profile,
            reminderSettings = reminderSettings,
            calendarRecords = records,
        ) as HomeUiState.Ready

        assertEquals(3, state.reminderItems.size)
        assertEquals(R.string.home_today_appointment_reminder, state.reminderItems[0].titleResId)
        assertEquals("10:30 - City Hospital", state.reminderItems[0].detail)
        assertEquals(CalendarRecordType.Appointment, state.reminderItems[0].actionRecordType)
        assertEquals(R.string.settings_daily_reminder_weight, state.reminderItems[1].titleResId)
        assertEquals(CalendarRecordType.Weight, state.reminderItems[1].actionRecordType)
        assertEquals(R.string.settings_daily_reminder_water, state.reminderItems[2].titleResId)
        assertEquals(null, state.reminderItems[2].actionRecordType)
    }

    @Test
    fun `ready state includes today's appointments when notification reminders are disabled`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )
        val reminderSettings = ReminderSettings(
            appointmentRemindersEnabled = false,
            dailyReminders = emptyList(),
        )

        val state = factory.create(
            profile = pregnancyProfile(),
            reminderSettings = reminderSettings,
            calendarRecords = listOf(
                appointmentRecord(
                    id = "today",
                    date = LocalDate.of(2026, 6, 21),
                    time = "10:30",
                    location = "City Hospital",
                ),
            ),
        ) as HomeUiState.Ready

        assertEquals(1, state.reminderItems.size)
        assertEquals(R.string.home_today_appointment_reminder, state.reminderItems.single().titleResId)
        assertEquals("10:30 - City Hospital", state.reminderItems.single().detail)
        assertEquals(CalendarRecordType.Appointment, state.reminderItems.single().actionRecordType)
    }

    @Test
    fun `ready state trims today's appointment detail from stored records`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )

        val state = factory.create(
            profile = pregnancyProfile(),
            calendarRecords = listOf(
                appointmentRecord(
                    id = "today",
                    date = LocalDate.of(2026, 6, 21),
                    time = " 10:30 ",
                    location = "  City Hospital  ",
                ),
            ),
        ) as HomeUiState.Ready

        assertEquals("10:30 - City Hospital", state.reminderItems.single().detail)
    }

    @Test
    fun `ready state sorts today's appointments by trimmed time`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )

        val state = factory.create(
            profile = pregnancyProfile(),
            calendarRecords = listOf(
                appointmentRecord(
                    id = "afternoon",
                    date = LocalDate.of(2026, 6, 21),
                    time = " 14:00 ",
                    location = "Clinic B",
                ),
                appointmentRecord(
                    id = "morning",
                    date = LocalDate.of(2026, 6, 21),
                    time = "09:00",
                    location = "Clinic A",
                ),
            ),
        ) as HomeUiState.Ready

        assertEquals("09:00 - Clinic A", state.reminderItems.first().detail)
    }

    @Test
    fun `ready state includes custom reminder message in home detail`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )
        val reminderSettings = ReminderSettings(
            appointmentRemindersEnabled = false,
            dailyReminders = listOf(
                DailyReminderPreference(
                    type = DailyReminderType.Custom,
                    enabled = true,
                    time = "20:00",
                    customMessage = "  Pack hospital bag  ",
                ),
            ),
        )

        val state = factory.create(
            profile = pregnancyProfile(),
            reminderSettings = reminderSettings,
        ) as HomeUiState.Ready

        assertEquals(R.string.settings_daily_reminder_custom, state.reminderItems.single().titleResId)
        assertEquals("20:00 - Pack hospital bag", state.reminderItems.single().detail)
    }

    private fun pregnancyProfile(): PregnancyProfile = PregnancyProfile(
        calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
        lmpDate = LocalDate.of(2026, 3, 1),
        dueDate = null,
        conceptionDate = null,
        gestationalWeekAtSetup = null,
        gestationalDayAtSetup = null,
        setupDate = LocalDate.of(2026, 6, 1),
    )

    private fun appointmentRecord(
        id: String,
        date: LocalDate,
        time: String,
        location: String,
    ): CalendarRecord = CalendarRecord(
        id = id,
        date = date,
        type = CalendarRecordType.Appointment,
        note = "",
        weightKg = null,
        fetalMovementCount = null,
        fetalMovementPeriod = null,
        fetalMovementFeeling = null,
        symptomType = null,
        symptomSeverity = null,
        exerciseType = null,
        exerciseMinutes = null,
        exerciseIntensity = null,
        dietMeal = null,
        dietContent = null,
        appointmentTime = time,
        appointmentLocation = location,
        appointmentDoctor = null,
        appointmentItems = null,
        appointmentResult = null,
        createdAtEpochMillis = 0L,
    )
}
