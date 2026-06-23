package com.yunqi.app.feature.setup

import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import com.yunqi.app.domain.pregnancy.PregnancyProgress
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
    val editableMethod = if (progress != null && !progress.isEditableCurrentGestationalAge()) {
        SetupMethod.DueDate
    } else {
        calculationMethod.toSetupMethod()
    }

    return PregnancySetupInput(
        method = editableMethod,
        lmpDate = lmpDate?.toString().orEmpty(),
        dueDate = when (editableMethod) {
            SetupMethod.DueDate -> (dueDate ?: progress?.dueDate)?.toString().orEmpty()
            SetupMethod.LastMenstrualPeriod,
            SetupMethod.ConceptionDate,
            SetupMethod.CurrentGestationalAge -> dueDate?.toString().orEmpty()
        },
        conceptionDate = conceptionDate?.toString().orEmpty(),
        week = if (editableMethod == SetupMethod.CurrentGestationalAge) progress?.week?.toString().orEmpty() else "",
        day = if (editableMethod == SetupMethod.CurrentGestationalAge) progress?.day?.toString().orEmpty() else "",
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

private fun PregnancyProgress.isEditableCurrentGestationalAge(): Boolean =
    week < MAX_EDITABLE_GESTATIONAL_WEEK ||
        (week == MAX_EDITABLE_GESTATIONAL_WEEK && day == 0L)

private const val MAX_EDITABLE_GESTATIONAL_WEEK = 42L
