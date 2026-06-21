package com.yunqi.app.feature.setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yunqi.app.data.local.PregnancyProfileRepository
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import java.time.LocalDate

class PregnancySetupViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PregnancyProfileRepository(application.applicationContext)

    suspend fun saveLastMenstrualPeriod(rawDate: String): SaveProfileResult {
        val lmpDate = rawDate.parseIsoDate()
            ?: return SaveProfileResult.InvalidDate

        repository.saveProfile(
            PregnancyProfile(
                calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
                lmpDate = lmpDate,
                dueDate = null,
                gestationalWeekAtSetup = null,
                gestationalDayAtSetup = null,
                setupDate = LocalDate.now(),
            ),
        )
        return SaveProfileResult.Success
    }

    suspend fun saveDueDate(rawDate: String): SaveProfileResult {
        val dueDate = rawDate.parseIsoDate()
            ?: return SaveProfileResult.InvalidDate

        repository.saveProfile(
            PregnancyProfile(
                calculationMethod = PregnancyCalculationMethod.DueDate,
                lmpDate = null,
                dueDate = dueDate,
                gestationalWeekAtSetup = null,
                gestationalDayAtSetup = null,
                setupDate = LocalDate.now(),
            ),
        )
        return SaveProfileResult.Success
    }

    suspend fun saveCurrentGestationalAge(
        rawWeek: String,
        rawDay: String,
    ): SaveProfileResult {
        val week = rawWeek.toIntOrNull()
            ?: return SaveProfileResult.InvalidWeek
        val day = rawDay.toIntOrNull()
            ?: return SaveProfileResult.InvalidDay

        if (week !in 0..42) return SaveProfileResult.InvalidWeek
        if (day !in 0..6) return SaveProfileResult.InvalidDay

        repository.saveProfile(
            PregnancyProfile(
                calculationMethod = PregnancyCalculationMethod.CurrentGestationalAge,
                lmpDate = null,
                dueDate = null,
                gestationalWeekAtSetup = week,
                gestationalDayAtSetup = day,
                setupDate = LocalDate.now(),
            ),
        )
        return SaveProfileResult.Success
    }

    private fun String.parseIsoDate(): LocalDate? = runCatching {
        LocalDate.parse(trim())
    }.getOrNull()
}

enum class SetupMethod {
    LastMenstrualPeriod,
    DueDate,
    CurrentGestationalAge,
}

sealed interface SaveProfileResult {
    data object Success : SaveProfileResult
    data object InvalidDate : SaveProfileResult
    data object InvalidWeek : SaveProfileResult
    data object InvalidDay : SaveProfileResult
}

