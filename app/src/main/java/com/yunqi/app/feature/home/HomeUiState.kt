package com.yunqi.app.feature.home

import com.yunqi.app.domain.pregnancy.PregnancyProgress

sealed interface HomeUiState {
    data object ProfileMissing : HomeUiState

    data class Ready(
        val progress: PregnancyProgress,
        val reminders: List<String>,
    ) : HomeUiState
}

