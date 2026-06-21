package com.yunqi.app.domain.pregnancy

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class PregnancyProfileTest {
    @Test
    fun `lmp profile calculates progress`() {
        val profile = PregnancyProfile(
            calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
            lmpDate = LocalDate.of(2026, 1, 1),
            dueDate = null,
            gestationalWeekAtSetup = null,
            gestationalDayAtSetup = null,
            setupDate = LocalDate.of(2026, 1, 10),
        )

        val progress = profile.calculateProgress(today = LocalDate.of(2026, 2, 1))

        assertEquals(4, progress.week)
        assertEquals(3, progress.day)
        assertEquals(LocalDate.of(2026, 10, 8), progress.dueDate)
    }

    @Test
    fun `due date profile calculates progress`() {
        val profile = PregnancyProfile(
            calculationMethod = PregnancyCalculationMethod.DueDate,
            lmpDate = null,
            dueDate = LocalDate.of(2026, 10, 8),
            gestationalWeekAtSetup = null,
            gestationalDayAtSetup = null,
            setupDate = LocalDate.of(2026, 1, 10),
        )

        val progress = profile.calculateProgress(today = LocalDate.of(2026, 2, 1))

        assertEquals(LocalDate.of(2026, 1, 1), progress.lmpDate)
        assertEquals(4, progress.week)
        assertEquals(3, progress.day)
    }

    @Test
    fun `current gestational age profile calculates progress`() {
        val profile = PregnancyProfile(
            calculationMethod = PregnancyCalculationMethod.CurrentGestationalAge,
            lmpDate = null,
            dueDate = null,
            gestationalWeekAtSetup = 10,
            gestationalDayAtSetup = 2,
            setupDate = LocalDate.of(2026, 6, 21),
        )

        val progress = profile.calculateProgress(today = LocalDate.of(2026, 6, 28))

        assertEquals(11, progress.week)
        assertEquals(2, progress.day)
    }
}

