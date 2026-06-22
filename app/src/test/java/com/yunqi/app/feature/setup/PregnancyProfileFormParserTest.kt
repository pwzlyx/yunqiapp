package com.yunqi.app.feature.setup

import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyBabyCount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PregnancyProfileFormParserTest {
    private val parser = PregnancyProfileFormParser(
        todayProvider = { LocalDate.of(2026, 6, 21) },
    )

    @Test
    fun `parses last menstrual period input`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.LastMenstrualPeriod,
                lmpDate = "2026-01-01",
                dueDate = "",
                conceptionDate = "",
                week = "",
                day = "",
            ),
        )

        assertTrue(result is PregnancyProfileParseResult.Success)
        val profile = (result as PregnancyProfileParseResult.Success).profile
        assertEquals(PregnancyCalculationMethod.LastMenstrualPeriod, profile.calculationMethod)
        assertEquals(LocalDate.of(2026, 1, 1), profile.lmpDate)
        assertEquals(LocalDate.of(2026, 6, 21), profile.setupDate)
    }

    @Test
    fun `parses due date input`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.DueDate,
                lmpDate = "",
                dueDate = "2026-10-08",
                conceptionDate = "",
                week = "",
                day = "",
            ),
        )

        assertTrue(result is PregnancyProfileParseResult.Success)
        val profile = (result as PregnancyProfileParseResult.Success).profile
        assertEquals(PregnancyCalculationMethod.DueDate, profile.calculationMethod)
        assertEquals(LocalDate.of(2026, 10, 8), profile.dueDate)
    }

    @Test
    fun `parses conception date input`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.ConceptionDate,
                lmpDate = "",
                dueDate = "",
                conceptionDate = "2026-01-15",
                week = "",
                day = "",
            ),
        )

        assertTrue(result is PregnancyProfileParseResult.Success)
        val profile = (result as PregnancyProfileParseResult.Success).profile
        assertEquals(PregnancyCalculationMethod.ConceptionDate, profile.calculationMethod)
        assertEquals(LocalDate.of(2026, 1, 15), profile.conceptionDate)
    }

    @Test
    fun `parses current gestational age input`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.CurrentGestationalAge,
                lmpDate = "",
                dueDate = "",
                conceptionDate = "",
                week = "10",
                day = "2",
            ),
        )

        assertTrue(result is PregnancyProfileParseResult.Success)
        val profile = (result as PregnancyProfileParseResult.Success).profile
        assertEquals(PregnancyCalculationMethod.CurrentGestationalAge, profile.calculationMethod)
        assertEquals(10, profile.gestationalWeekAtSetup)
        assertEquals(2, profile.gestationalDayAtSetup)
    }

    @Test
    fun `preserves exercise restriction setting`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.LastMenstrualPeriod,
                lmpDate = "2026-01-01",
                dueDate = "",
                conceptionDate = "",
                week = "",
                day = "",
                exerciseRestricted = true,
            ),
        )

        val profile = (result as PregnancyProfileParseResult.Success).profile
        assertEquals(true, profile.exerciseRestricted)
    }

    @Test
    fun `parses optional profile details`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.LastMenstrualPeriod,
                lmpDate = "2026-01-01",
                dueDate = "",
                conceptionDate = "",
                week = "",
                day = "",
                heightCm = " 168.5 ",
                prePregnancyWeightKg = "55.2",
                babyCount = PregnancyBabyCount.Twin,
            ),
        )

        val profile = (result as PregnancyProfileParseResult.Success).profile
        assertEquals(168.5, profile.heightCm ?: 0.0, 0.001)
        assertEquals(55.2, profile.prePregnancyWeightKg ?: 0.0, 0.001)
        assertEquals(PregnancyBabyCount.Twin, profile.babyCount)
    }

    @Test
    fun `rejects invalid date input`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.LastMenstrualPeriod,
                lmpDate = "2026/01/01",
                dueDate = "",
                conceptionDate = "",
                week = "",
                day = "",
            ),
        )

        assertEquals(PregnancyProfileParseResult.InvalidDate, result)
    }

    @Test
    fun `rejects out of range gestational week`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.CurrentGestationalAge,
                lmpDate = "",
                dueDate = "",
                conceptionDate = "",
                week = "43",
                day = "0",
            ),
        )

        assertEquals(PregnancyProfileParseResult.InvalidWeek, result)
    }

    @Test
    fun `allows exactly forty two weeks and zero days`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.CurrentGestationalAge,
                lmpDate = "",
                dueDate = "",
                conceptionDate = "",
                week = "42",
                day = "0",
            ),
        )

        assertTrue(result is PregnancyProfileParseResult.Success)
    }

    @Test
    fun `rejects gestational age beyond forty two weeks`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.CurrentGestationalAge,
                lmpDate = "",
                dueDate = "",
                conceptionDate = "",
                week = "42",
                day = "1",
            ),
        )

        assertEquals(PregnancyProfileParseResult.InvalidWeek, result)
    }

    @Test
    fun `rejects out of range gestational day`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.CurrentGestationalAge,
                lmpDate = "",
                dueDate = "",
                conceptionDate = "",
                week = "12",
                day = "7",
            ),
        )

        assertEquals(PregnancyProfileParseResult.InvalidDay, result)
    }

    @Test
    fun `rejects invalid optional height`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.LastMenstrualPeriod,
                lmpDate = "2026-01-01",
                dueDate = "",
                conceptionDate = "",
                week = "",
                day = "",
                heightCm = "0",
            ),
        )

        assertEquals(PregnancyProfileParseResult.InvalidHeight, result)
    }

    @Test
    fun `rejects invalid optional pre pregnancy weight`() {
        val result = parser.parse(
            PregnancySetupInput(
                method = SetupMethod.LastMenstrualPeriod,
                lmpDate = "2026-01-01",
                dueDate = "",
                conceptionDate = "",
                week = "",
                day = "",
                prePregnancyWeightKg = "abc",
            ),
        )

        assertEquals(PregnancyProfileParseResult.InvalidPrePregnancyWeight, result)
    }
}
