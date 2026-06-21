package com.yunqi.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class YunqiAppSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

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
    }
}
