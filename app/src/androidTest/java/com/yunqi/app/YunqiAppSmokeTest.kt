package com.yunqi.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performScrollTo
import com.yunqi.app.data.export.CalendarRecordExportStore
import com.yunqi.app.data.local.ContentStatusRepository
import com.yunqi.app.data.local.PregnancyProfileRepository
import com.yunqi.app.data.local.ReminderSettingsRepository
import com.yunqi.app.data.local.record.CalendarRecordRepository
import com.yunqi.app.feature.calendar.CalendarTestTags
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class YunqiAppSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearLocalPregnancyData() {
        val context = composeRule.activity.applicationContext

        runBlocking {
            CalendarRecordRepository(context).deleteAll()
            PregnancyProfileRepository(context).clearProfile()
            ReminderSettingsRepository(context).clearSettings()
            ContentStatusRepository(context).clearStatus()
        }
        CalendarRecordExportStore().clear(context.cacheDir)
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule
                .onAllNodesWithText(composeRule.activity.getString(R.string.home_profile_missing_title))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun bottomNavigationOpensCoreScreens() {
        val activity = composeRule.activity

        composeRule.onNodeWithText(activity.getString(R.string.nav_home)).assertIsDisplayed()
        composeRule.onNodeWithText(activity.getString(R.string.home_profile_missing_title)).assertIsDisplayed()

        composeRule.onNodeWithText(activity.getString(R.string.nav_calendar)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.calendar_title)).assertIsDisplayed()

        composeRule.onNodeWithText(activity.getString(R.string.nav_trends)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.trends_title)).assertIsDisplayed()

        composeRule.onNodeWithText(activity.getString(R.string.nav_settings)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.settings_title)).assertIsDisplayed()
        composeRule.onNodeWithText(activity.getString(R.string.medical_disclaimer))
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithText(activity.getString(R.string.emergency_attention_notice))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun pregnancySetupShowsDueDatePreviewFromLastMenstrualPeriod() {
        val activity = composeRule.activity

        composeRule.onNodeWithText(activity.getString(R.string.nav_settings)).performClick()
        composeRule.onNodeWithText(activity.getString(R.string.settings_edit_pregnancy_profile))
            .performScrollTo()
            .performClick()

        composeRule.onNodeWithText(activity.getString(R.string.setup_title)).assertIsDisplayed()
        composeRule.onNodeWithText(activity.getString(R.string.setup_method_lmp)).performClick()
        val lmpField = composeRule.onNodeWithText(activity.getString(R.string.setup_lmp_label))
        lmpField.performTextClearance()
        lmpField.performTextInput("2026-03-01")

        composeRule.onNodeWithText(activity.getString(R.string.setup_preview_title))
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithText(activity.getString(R.string.setup_preview_due_date, "2026-12-06"))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun homeDietQuickRecordOpensDietCalendarForm() {
        val activity = composeRule.activity

        runBlocking {
            PregnancyProfileRepository(activity.applicationContext).saveProfile(
                PregnancyProfile(
                    calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
                    lmpDate = LocalDate.of(2026, 3, 1),
                    dueDate = null,
                    conceptionDate = null,
                    gestationalWeekAtSetup = null,
                    gestationalDayAtSetup = null,
                    setupDate = LocalDate.of(2026, 3, 1),
                ),
            )
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule
                .onAllNodesWithText(activity.getString(R.string.home_today))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeRule.onNodeWithText(activity.getString(R.string.home_reminder_diet))
            .performScrollTo()
            .performClick()

        composeRule.onNodeWithText(activity.getString(R.string.calendar_title)).assertIsDisplayed()
        composeRule.onNodeWithText(activity.getString(R.string.calendar_diet_content_label))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun calendarCreatesEditsAndDeletesWeightRecord() {
        val activity = composeRule.activity
        val initialWeight = activity.getString(R.string.calendar_record_weight_value, 62.5)
        val updatedWeight = activity.getString(R.string.calendar_record_weight_value, 63.1)

        composeRule.onNodeWithText(activity.getString(R.string.nav_calendar)).performClick()
        composeRule.onNodeWithTag(CalendarTestTags.WeightRecordType)
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithTag(CalendarTestTags.WeightField)
            .performScrollTo()
            .performTextInput("62.5")
        composeRule.onNodeWithTag(CalendarTestTags.SaveRecordButton)
            .performScrollTo()
            .performClick()
        waitUntilTextExists(initialWeight)

        composeRule.onAllNodesWithTag(CalendarTestTags.EditRecordButton)[0]
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithTag(CalendarTestTags.WeightField)
            .performScrollTo()
            .performTextClearance()
        composeRule.onNodeWithTag(CalendarTestTags.WeightField)
            .performTextInput("63.1")
        composeRule.onNodeWithTag(CalendarTestTags.SaveRecordButton)
            .performScrollTo()
            .performClick()
        waitUntilTextExists(updatedWeight)
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(initialWeight).fetchSemanticsNodes().isEmpty()
        }

        composeRule.onAllNodesWithTag(CalendarTestTags.DeleteRecordButton)[0]
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithTag(CalendarTestTags.ConfirmDeleteButton).performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(updatedWeight).fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithText(activity.getString(R.string.calendar_empty_records))
            .performScrollTo()
            .assertIsDisplayed()
    }

    private fun waitUntilTextExists(text: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(text)
            .performScrollTo()
            .assertIsDisplayed()
    }
}
