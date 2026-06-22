package com.yunqi.app.feature.setup

import com.yunqi.app.domain.pregnancy.PregnancyBabyCount
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class PregnancySetupInputMapperTest {
    @Test
    fun `maps saved profile details into editable form input`() {
        val input = PregnancyProfile(
            calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
            lmpDate = LocalDate.of(2026, 1, 1),
            dueDate = null,
            conceptionDate = null,
            gestationalWeekAtSetup = null,
            gestationalDayAtSetup = null,
            exerciseRestricted = true,
            heightCm = 168.0,
            prePregnancyWeightKg = 55.5,
            babyCount = PregnancyBabyCount.Twin,
            setupDate = LocalDate.of(2026, 1, 1),
        ).toPregnancySetupInput(today = LocalDate.of(2026, 6, 21))

        assertEquals(SetupMethod.LastMenstrualPeriod, input.method)
        assertEquals("2026-01-01", input.lmpDate)
        assertEquals("", input.dueDate)
        assertEquals(true, input.exerciseRestricted)
        assertEquals("168", input.heightCm)
        assertEquals("55.5", input.prePregnancyWeightKg)
        assertEquals(PregnancyBabyCount.Twin, input.babyCount)
    }

    @Test
    fun `maps current gestational age profile to today's editable week and day`() {
        val input = PregnancyProfile(
            calculationMethod = PregnancyCalculationMethod.CurrentGestationalAge,
            lmpDate = null,
            dueDate = null,
            conceptionDate = null,
            gestationalWeekAtSetup = 10,
            gestationalDayAtSetup = 2,
            setupDate = LocalDate.of(2026, 6, 1),
        ).toPregnancySetupInput(today = LocalDate.of(2026, 6, 22))

        assertEquals(SetupMethod.CurrentGestationalAge, input.method)
        assertEquals("13", input.week)
        assertEquals("2", input.day)
    }
}
