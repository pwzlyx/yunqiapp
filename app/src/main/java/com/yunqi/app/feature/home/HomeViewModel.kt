package com.yunqi.app.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.local.PregnancyProfileRepository
import com.yunqi.app.domain.pregnancy.calculateProgress
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PregnancyProfileRepository(application.applicationContext)

    val uiState: StateFlow<HomeUiState> = repository.profileFlow
        .map { profile ->
            if (profile == null) {
                HomeUiState.ProfileMissing
            } else {
                HomeUiState.Ready(
                    progress = profile.calculateProgress(today = LocalDate.now()),
                    calculationMethod = profile.calculationMethod,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.ProfileMissing,
        )
}

