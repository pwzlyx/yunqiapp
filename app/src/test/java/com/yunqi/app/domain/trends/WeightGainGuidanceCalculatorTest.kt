package com.yunqi.app.domain.trends

import com.yunqi.app.domain.pregnancy.PregnancyBabyCount
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeightGainGuidanceCalculatorTest {
    @Test
    fun `calculates singleton normal BMI guidance`() {
        val guidance = WeightGainGuidanceCalculator.calculate(
            profile(heightCm = 165.0, prePregnancyWeightKg = 60.0),
        )

        requireNotNull(guidance)
        assertEquals(22.0, guidance.bmi, 0.1)
        assertEquals(PrePregnancyBmiCategory.Normal, guidance.category)
        assertEquals(11.3, guidance.minGainKg, 0.001)
        assertEquals(15.9, guidance.maxGainKg, 0.001)
    }

    @Test
    fun `calculates twin overweight BMI guidance`() {
        val guidance = WeightGainGuidanceCalculator.calculate(
            profile(
                heightCm = 160.0,
                prePregnancyWeightKg = 70.0,
                babyCount = PregnancyBabyCount.Twin,
            ),
        )

        requireNotNull(guidance)
        assertEquals(27.3, guidance.bmi, 0.1)
        assertEquals(PrePregnancyBmiCategory.Overweight, guidance.category)
        assertEquals(14.1, guidance.minGainKg, 0.001)
        assertEquals(22.7, guidance.maxGainKg, 0.001)
    }

    @Test
    fun `returns null when profile details are missing`() {
        assertNull(
            WeightGainGuidanceCalculator.calculate(
                profile(heightCm = null, prePregnancyWeightKg = 60.0),
            ),
        )
        assertNull(
            WeightGainGuidanceCalculator.calculate(
                profile(heightCm = 165.0, prePregnancyWeightKg = null),
            ),
        )
    }

    private fun profile(
        heightCm: Double?,
        prePregnancyWeightKg: Double?,
        babyCount: PregnancyBabyCount = PregnancyBabyCount.Singleton,
    ): PregnancyProfile = PregnancyProfile(
        calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
        lmpDate = LocalDate.of(2026, 1, 1),
        dueDate = null,
        conceptionDate = null,
        gestationalWeekAtSetup = null,
        gestationalDayAtSetup = null,
        exerciseRestricted = false,
        heightCm = heightCm,
        prePregnancyWeightKg = prePregnancyWeightKg,
        babyCount = babyCount,
        setupDate = LocalDate.of(2026, 6, 21),
    )
}
