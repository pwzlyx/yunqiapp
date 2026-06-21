package com.yunqi.app.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.local.PregnancyProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val profileRepository = PregnancyProfileRepository(application.applicationContext)
    private val stateFactory = HomeUiStateFactory()

    /**
     * Converts the local pregnancy profile stream into render-ready home screen state.
     */
    val uiState: StateFlow<HomeUiState> = profileRepository.profileFlow
        .map(stateFactory::create)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.ProfileMissing,
        )
}
