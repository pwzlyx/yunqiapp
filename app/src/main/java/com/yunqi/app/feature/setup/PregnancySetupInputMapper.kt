package com.yunqi.app.feature.setup

import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import com.yunqi.app.domain.pregnancy.calculateProgress
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Converts a saved local profile back into editable setup form fields.
 */
fun PregnancyProfile.toPregnancySetupInput(today: LocalDate): PregnancySetupInput {
    val progress = if (calculationMethod == PregnancyCalculationMethod.CurrentGestationalAge) {
        calculateProgress(today = today)
    } else {
        null
    }

    return PregnancySetupInput(
        method = calculationMethod.toSetupMethod(),
        lmpDate = lmpDate?.toString().orEmpty(),
        dueDate = dueDate?.toString().orEmpty(),
        conceptionDate = conceptionDate?.toString().orEmpty(),
        week = progress?.week?.toString().orEmpty(),
        day = progress?.day?.toString().orEmpty(),
        exerciseRestricted = exerciseRestricted,
        heightCm = heightCm.toEditableDecimal(),
        prePregnancyWeightKg = prePregnancyWeightKg.toEditableDecimal(),
        babyCount = babyCount,
    )
}

private fun PregnancyCalculationMethod.toSetupMethod(): SetupMethod = when (this) {
    PregnancyCalculationMethod.LastMenstrualPeriod -> SetupMethod.LastMenstrualPeriod
    PregnancyCalculationMethod.DueDate -> SetupMethod.DueDate
    PregnancyCalculationMethod.ConceptionDate -> SetupMethod.ConceptionDate
    PregnancyCalculationMethod.CurrentGestationalAge -> SetupMethod.CurrentGestationalAge
}

private fun Double?.toEditableDecimal(): String =
    this?.let { BigDecimal.valueOf(it).stripTrailingZeros().toPlainString() }.orEmpty()
