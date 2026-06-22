package com.yunqi.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
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
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
}
