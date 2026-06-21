package com.yunqi.app.feature.trends

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.local.record.CalendarRecordRepository
import com.yunqi.app.domain.trends.TrendSummary
import com.yunqi.app.domain.trends.TrendSummaryCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TrendsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CalendarRecordRepository(application.applicationContext)

    /**
     * Emits aggregate trend statistics from locally stored calendar records.
     */
    val uiState: StateFlow<TrendsUiState> = repository.allRecords()
        .map { records ->
            TrendsUiState(summary = TrendSummaryCalculator.calculate(records))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrendsUiState(summary = TrendSummaryCalculator.calculate(emptyList())),
        )
}

data class TrendsUiState(
    val summary: TrendSummary,
)

