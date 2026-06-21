package com.yunqi.app.feature.home

import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    // Until local persistence is implemented, use an explicit empty state so
    // the home screen never presents fabricated pregnancy progress as real data.
    val uiState: HomeUiState = HomeUiState.ProfileMissing
}

