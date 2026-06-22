package com.yunqi.app.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.local.ContentStatusRepository
import com.yunqi.app.data.local.PregnancyProfileRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val profileRepository = PregnancyProfileRepository(application.applicationContext)
    private val contentStatusRepository = ContentStatusRepository(application.applicationContext)
    private val stateFactory = HomeUiStateFactory()

    /**
     * Converts the local pregnancy profile stream into render-ready home screen state.
     */
    val uiState: StateFlow<HomeUiState> = combine(
        profileRepository.profileFlow,
        contentStatusRepository.statusFlow,
        stateFactory::create,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.ProfileMissing,
        )

    fun toggleContentRead(contentId: String) {
        viewModelScope.launch {
            contentStatusRepository.toggleRead(contentId)
        }
    }

    fun toggleContentFavorite(contentId: String) {
        viewModelScope.launch {
            contentStatusRepository.toggleFavorite(contentId)
        }
    }

    fun toggleContentHidden(contentId: String) {
        viewModelScope.launch {
            contentStatusRepository.toggleHidden(contentId)
        }
    }
}
