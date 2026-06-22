package com.yunqi.app.feature.setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunqi.app.data.local.PregnancyProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class PregnancySetupViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PregnancyProfileRepository(application.applicationContext)
    private val parser = PregnancyProfileFormParser()

    val savedInput: StateFlow<PregnancySetupInput?> = repository.profileFlow
        .map { profile -> profile?.toPregnancySetupInput(today = LocalDate.now()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    /**
     * Validates the setup form and persists the profile locally when valid.
     */
    suspend fun save(input: PregnancySetupInput): SaveProfileResult = when (val result = parser.parse(input)) {
        is PregnancyProfileParseResult.Success -> {
            repository.saveProfile(result.profile)
            SaveProfileResult.Success
        }

        PregnancyProfileParseResult.InvalidDate -> SaveProfileResult.InvalidDate
        PregnancyProfileParseResult.InvalidWeek -> SaveProfileResult.InvalidWeek
        PregnancyProfileParseResult.InvalidDay -> SaveProfileResult.InvalidDay
        PregnancyProfileParseResult.InvalidHeight -> SaveProfileResult.InvalidHeight
        PregnancyProfileParseResult.InvalidPrePregnancyWeight -> SaveProfileResult.InvalidPrePregnancyWeight
    }
}

enum class SetupMethod {
    LastMenstrualPeriod,
    DueDate,
    ConceptionDate,
    CurrentGestationalAge,
}

sealed interface SaveProfileResult {
    data object Success : SaveProfileResult
    data object InvalidDate : SaveProfileResult
    data object InvalidWeek : SaveProfileResult
    data object InvalidDay : SaveProfileResult
    data object InvalidHeight : SaveProfileResult
    data object InvalidPrePregnancyWeight : SaveProfileResult
}
