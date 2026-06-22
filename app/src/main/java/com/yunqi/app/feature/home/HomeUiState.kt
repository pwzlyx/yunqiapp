package com.yunqi.app.feature.home

import androidx.annotation.StringRes
import com.yunqi.app.data.content.PregnancyContentCard
import com.yunqi.app.data.local.ContentStatus
import com.yunqi.app.domain.calendar.CalendarRecordType
import com.yunqi.app.domain.pregnancy.PregnancyProgress
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod

sealed interface HomeUiState {
    data object ProfileMissing : HomeUiState

    data class Ready(
        val progress: PregnancyProgress,
        val calculationMethod: PregnancyCalculationMethod,
        val exerciseRestricted: Boolean,
        val contentCards: List<PregnancyContentCard>,
        val reminderItems: List<HomeReminderItem> = emptyList(),
        val contentStatus: ContentStatus = ContentStatus(),
    ) : HomeUiState
}

data class HomeReminderItem(
    val id: String,
    @StringRes val titleResId: Int,
    val detail: String?,
    val actionRecordType: CalendarRecordType?,
)
