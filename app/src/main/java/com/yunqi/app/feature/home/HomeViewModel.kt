package com.yunqi.app.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.content.PregnancyContentRepository
import com.yunqi.app.data.local.PregnancyProfileRepository
import com.yunqi.app.domain.pregnancy.calculateProgress
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val profileRepository = PregnancyProfileRepository(application.applicationContext)
    private val contentRepository = PregnancyContentRepository()

    /**
     * Converts the local pregnancy profile stream into render-ready home screen state.
     */
    val uiState: StateFlow<HomeUiState> = profileRepository.profileFlow
        .map { profile ->
            if (profile == null) {
                HomeUiState.ProfileMissing
            } else {
                val progress = profile.calculateProgress(today = LocalDate.now())
                HomeUiState.Ready(
                    progress = progress,
                    calculationMethod = profile.calculationMethod,
                    contentCards = contentRepository.cardsForWeek(progress.week),
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.ProfileMissing,
        )
}
