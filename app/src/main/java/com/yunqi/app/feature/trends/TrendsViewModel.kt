package com.yunqi.app.feature.trends

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.local.record.CalendarRecordRepository
import com.yunqi.app.domain.trends.TrendRange
import com.yunqi.app.domain.trends.TrendRecordFilter
import com.yunqi.app.domain.trends.TrendSummary
import com.yunqi.app.domain.trends.TrendSummaryCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class TrendsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CalendarRecordRepository(application.applicationContext)
    private val selectedRange = MutableStateFlow(TrendRange.All)

    /**
     * Emits aggregate trend statistics from locally stored calendar records.
     */
    val uiState: StateFlow<TrendsUiState> = combine(
        repository.allRecords(),
        selectedRange,
    ) { records, range ->
        val filteredRecords = TrendRecordFilter.filter(
            records = records,
            range = range,
            today = LocalDate.now(),
        )
        TrendsUiState(
            selectedRange = range,
            summary = TrendSummaryCalculator.calculate(filteredRecords),
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrendsUiState(
                selectedRange = TrendRange.All,
                summary = TrendSummaryCalculator.calculate(emptyList()),
            ),
        )

    fun selectRange(range: TrendRange) {
        selectedRange.value = range
    }
}

data class TrendsUiState(
    val selectedRange: TrendRange,
    val summary: TrendSummary,
)
