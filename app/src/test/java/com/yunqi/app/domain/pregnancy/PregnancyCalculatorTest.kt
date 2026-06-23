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
    fun `calculates progress from conception date`() {
        val result = PregnancyCalculator.fromConceptionDate(
            conceptionDate = LocalDate.of(2026, 1, 15),
            today = LocalDate.of(2026, 2, 1),
        )

        assertEquals(LocalDate.of(2026, 1, 1), result.lmpDate)
        assertEquals(LocalDate.of(2026, 10, 8), result.dueDate)
        assertEquals(4, result.week)
        assertEquals(3, result.day)
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

    @Test
    fun `maps gestational age to trimester boundaries`() {
        val lmpDate = LocalDate.of(2026, 1, 1)

        assertEquals(
            Trimester.First,
            PregnancyCalculator.fromLastMenstrualPeriod(lmpDate, lmpDate.plusWeeks(13)).trimester,
        )
        assertEquals(
            Trimester.Second,
            PregnancyCalculator.fromLastMenstrualPeriod(lmpDate, lmpDate.plusWeeks(14)).trimester,
        )
        assertEquals(
            Trimester.Second,
            PregnancyCalculator.fromLastMenstrualPeriod(lmpDate, lmpDate.plusWeeks(27)).trimester,
        )
        assertEquals(
            Trimester.Third,
            PregnancyCalculator.fromLastMenstrualPeriod(lmpDate, lmpDate.plusWeeks(28)).trimester,
        )
        assertEquals(
            Trimester.Third,
            PregnancyCalculator.fromLastMenstrualPeriod(lmpDate, lmpDate.plusWeeks(42)).trimester,
        )
        assertEquals(
            Trimester.PostDue,
            PregnancyCalculator.fromLastMenstrualPeriod(lmpDate, lmpDate.plusWeeks(43)).trimester,
        )
    }

    @Test
    fun `keeps negative due date countdown after due date`() {
        val result = PregnancyCalculator.fromLastMenstrualPeriod(
            lmpDate = LocalDate.of(2026, 1, 1),
            today = LocalDate.of(2026, 10, 10),
        )

        assertEquals(-2, result.daysUntilDueDate)
    }
}
