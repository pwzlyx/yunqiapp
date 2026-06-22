package com.yunqi.app.feature.setup

import com.yunqi.app.domain.pregnancy.PregnancyBabyCount
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import java.time.LocalDate

/**
 * Converts raw setup form values into a validated [PregnancyProfile].
 *
 * Keeping this parser free of Android framework types makes the pregnancy setup
 * rules easy to unit test and keeps the composable focused on rendering state.
 */
class PregnancyProfileFormParser(
    private val todayProvider: () -> LocalDate = LocalDate::now,
) {
    fun parse(input: PregnancySetupInput): PregnancyProfileParseResult {
        val setupDate = todayProvider()
        val heightCm = input.heightCm.parseOptionalDoubleInRange(
            minInclusive = MIN_HEIGHT_CM,
            maxInclusive = MAX_HEIGHT_CM,
        )
        if (input.heightCm.isNotBlank() && heightCm == null) {
            return PregnancyProfileParseResult.InvalidHeight
        }
        val prePregnancyWeightKg = input.prePregnancyWeightKg.parseOptionalDoubleInRange(
            minInclusive = MIN_PRE_PREGNANCY_WEIGHT_KG,
            maxInclusive = MAX_PRE_PREGNANCY_WEIGHT_KG,
        )
        if (input.prePregnancyWeightKg.isNotBlank() && prePregnancyWeightKg == null) {
            return PregnancyProfileParseResult.InvalidPrePregnancyWeight
        }

        return when (input.method) {
            SetupMethod.LastMenstrualPeriod -> {
                val lmpDate = input.lmpDate.parseIsoDate()
                    ?: return PregnancyProfileParseResult.InvalidDate
                PregnancyProfileParseResult.Success(
                    PregnancyProfile(
                        calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
                        lmpDate = lmpDate,
                        dueDate = null,
                        conceptionDate = null,
                        gestationalWeekAtSetup = null,
                        gestationalDayAtSetup = null,
                        exerciseRestricted = input.exerciseRestricted,
                        heightCm = heightCm,
                        prePregnancyWeightKg = prePregnancyWeightKg,
                        babyCount = input.babyCount,
                        setupDate = setupDate,
                    ),
                )
            }

            SetupMethod.DueDate -> {
                val dueDate = input.dueDate.parseIsoDate()
                    ?: return PregnancyProfileParseResult.InvalidDate
                PregnancyProfileParseResult.Success(
                    PregnancyProfile(
                        calculationMethod = PregnancyCalculationMethod.DueDate,
                        lmpDate = null,
                        dueDate = dueDate,
                        conceptionDate = null,
                        gestationalWeekAtSetup = null,
                        gestationalDayAtSetup = null,
                        exerciseRestricted = input.exerciseRestricted,
                        heightCm = heightCm,
                        prePregnancyWeightKg = prePregnancyWeightKg,
                        babyCount = input.babyCount,
                        setupDate = setupDate,
                    ),
                )
            }

            SetupMethod.ConceptionDate -> {
                val conceptionDate = input.conceptionDate.parseIsoDate()
                    ?: return PregnancyProfileParseResult.InvalidDate
                PregnancyProfileParseResult.Success(
                    PregnancyProfile(
                        calculationMethod = PregnancyCalculationMethod.ConceptionDate,
                        lmpDate = null,
                        dueDate = null,
                        conceptionDate = conceptionDate,
                        gestationalWeekAtSetup = null,
                        gestationalDayAtSetup = null,
                        exerciseRestricted = input.exerciseRestricted,
                        heightCm = heightCm,
                        prePregnancyWeightKg = prePregnancyWeightKg,
                        babyCount = input.babyCount,
                        setupDate = setupDate,
                    ),
                )
            }

            SetupMethod.CurrentGestationalAge -> {
                val week = input.week.toIntOrNull()
                    ?: return PregnancyProfileParseResult.InvalidWeek
                val day = input.day.toIntOrNull()
                    ?: return PregnancyProfileParseResult.InvalidDay

                if (week !in 0..42) return PregnancyProfileParseResult.InvalidWeek
                if (day !in 0..6) return PregnancyProfileParseResult.InvalidDay
                if (week == MAX_GESTATIONAL_WEEK && day > 0) {
                    return PregnancyProfileParseResult.InvalidWeek
                }

                PregnancyProfileParseResult.Success(
                    PregnancyProfile(
                        calculationMethod = PregnancyCalculationMethod.CurrentGestationalAge,
                        lmpDate = null,
                        dueDate = null,
                        conceptionDate = null,
                        gestationalWeekAtSetup = week,
                        gestationalDayAtSetup = day,
                        exerciseRestricted = input.exerciseRestricted,
                        heightCm = heightCm,
                        prePregnancyWeightKg = prePregnancyWeightKg,
                        babyCount = input.babyCount,
                        setupDate = setupDate,
                    ),
                )
            }
        }
    }

    private fun String.parseIsoDate(): LocalDate? = runCatching {
        LocalDate.parse(trim())
    }.getOrNull()

    private fun String.parseOptionalDoubleInRange(
        minInclusive: Double,
        maxInclusive: Double,
    ): Double? {
        if (isBlank()) return null
        val value = trim().toDoubleOrNull() ?: return null
        return value.takeIf { it in minInclusive..maxInclusive }
    }

    private companion object {
        const val MAX_GESTATIONAL_WEEK = 42
        const val MIN_HEIGHT_CM = 100.0
        const val MAX_HEIGHT_CM = 250.0
        const val MIN_PRE_PREGNANCY_WEIGHT_KG = 25.0
        const val MAX_PRE_PREGNANCY_WEIGHT_KG = 300.0
    }
}

data class PregnancySetupInput(
    val method: SetupMethod,
    val lmpDate: String,
    val dueDate: String,
    val conceptionDate: String,
    val week: String,
    val day: String,
    val exerciseRestricted: Boolean = false,
    val heightCm: String = "",
    val prePregnancyWeightKg: String = "",
    val babyCount: PregnancyBabyCount = PregnancyBabyCount.Singleton,
)

sealed interface PregnancyProfileParseResult {
    data class Success(val profile: PregnancyProfile) : PregnancyProfileParseResult
    data object InvalidDate : PregnancyProfileParseResult
    data object InvalidWeek : PregnancyProfileParseResult
    data object InvalidDay : PregnancyProfileParseResult
    data object InvalidHeight : PregnancyProfileParseResult
    data object InvalidPrePregnancyWeight : PregnancyProfileParseResult
}
