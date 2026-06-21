package com.yunqi.app.domain.pregnancy

import java.time.LocalDate

enum class PregnancyCalculationMethod {
    LastMenstrualPeriod,
    DueDate,
    CurrentGestationalAge,
}

data class PregnancyProfile(
    val calculationMethod: PregnancyCalculationMethod,
    val lmpDate: LocalDate?,
    val dueDate: LocalDate?,
    val gestationalWeekAtSetup: Int?,
    val gestationalDayAtSetup: Int?,
    val setupDate: LocalDate,
)

/**
 * Calculates current progress from a persisted local pregnancy profile.
 */
fun PregnancyProfile.calculateProgress(today: LocalDate): PregnancyProgress = when (calculationMethod) {
    PregnancyCalculationMethod.LastMenstrualPeriod -> {
        PregnancyCalculator.fromLastMenstrualPeriod(
            lmpDate = requireNotNull(lmpDate) { "lmpDate is required" },
            today = today,
        )
    }

    PregnancyCalculationMethod.DueDate -> {
        PregnancyCalculator.fromDueDate(
            dueDate = requireNotNull(dueDate) { "dueDate is required" },
            today = today,
        )
    }

    PregnancyCalculationMethod.CurrentGestationalAge -> {
        PregnancyCalculator.fromCurrentGestationalAge(
            week = requireNotNull(gestationalWeekAtSetup) { "gestationalWeekAtSetup is required" },
            day = requireNotNull(gestationalDayAtSetup) { "gestationalDayAtSetup is required" },
            setupDate = setupDate,
            today = today,
        )
    }
}
