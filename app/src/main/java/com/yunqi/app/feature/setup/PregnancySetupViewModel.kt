package com.yunqi.app.feature.setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yunqi.app.data.local.PregnancyProfileRepository

class PregnancySetupViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PregnancyProfileRepository(application.applicationContext)
    private val parser = PregnancyProfileFormParser()

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
}
