package com.yunqi.app.feature.setup

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

                PregnancyProfileParseResult.Success(
                    PregnancyProfile(
                        calculationMethod = PregnancyCalculationMethod.CurrentGestationalAge,
                        lmpDate = null,
                        dueDate = null,
                        conceptionDate = null,
                        gestationalWeekAtSetup = week,
                        gestationalDayAtSetup = day,
                        exerciseRestricted = input.exerciseRestricted,
                        setupDate = setupDate,
                    ),
                )
            }
        }
    }

    private fun String.parseIsoDate(): LocalDate? = runCatching {
        LocalDate.parse(trim())
    }.getOrNull()
}

data class PregnancySetupInput(
    val method: SetupMethod,
    val lmpDate: String,
    val dueDate: String,
    val conceptionDate: String,
    val week: String,
    val day: String,
    val exerciseRestricted: Boolean = false,
)

sealed interface PregnancyProfileParseResult {
    data class Success(val profile: PregnancyProfile) : PregnancyProfileParseResult
    data object InvalidDate : PregnancyProfileParseResult
    data object InvalidWeek : PregnancyProfileParseResult
    data object InvalidDay : PregnancyProfileParseResult
}
