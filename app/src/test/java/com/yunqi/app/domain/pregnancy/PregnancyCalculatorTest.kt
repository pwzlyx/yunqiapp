package com.yunqi.app.domain.pregnancy

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class PregnancyCalculatorTest {
    @Test
    fun `calculates gestational age from lmp`() {
        val result = PregnancyCalculator.fromLastMenstrualPeriod(
            lmpDate = LocalDate.of(2026, 1, 1),
            today = LocalDate.of(2026, 2, 1),
        )

        assertEquals(31, result.gestationalDays)
        assertEquals(4, result.week)
        assertEquals(3, result.day)
        assertEquals(LocalDate.of(2026, 10, 8), result.dueDate)
    }

    @Test
    fun `calculates lmp from due date`() {
        val result = PregnancyCalculator.fromDueDate(
            dueDate = LocalDate.of(2026, 10, 8),
            today = LocalDate.of(2026, 2, 1),
        )

        assertEquals(LocalDate.of(2026, 1, 1), result.lmpDate)
    }

    @Test
    fun `calculates lmp from current gestational age`() {
        val result = PregnancyCalculator.fromCurrentGestationalAge(
            week = 10,
            day = 2,
            setupDate = LocalDate.of(2026, 6, 21),
            today = LocalDate.of(2026, 6, 21),
        )

        assertEquals(LocalDate.of(2026, 4, 10), result.lmpDate)
        assertEquals(10, result.week)
        assertEquals(2, result.day)
    }
}

