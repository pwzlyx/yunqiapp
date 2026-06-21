package com.yunqi.app.feature.home

import com.yunqi.app.data.content.PregnancyContentCard
import com.yunqi.app.domain.pregnancy.PregnancyProgress
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod

sealed interface HomeUiState {
    data object ProfileMissing : HomeUiState

    data class Ready(
        val progress: PregnancyProgress,
        val calculationMethod: PregnancyCalculationMethod,
        val contentCards: List<PregnancyContentCard>,
    ) : HomeUiState
}
