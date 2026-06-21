package com.yunqi.app.domain.pregnancy

import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val FULL_TERM_DAYS = 280L

enum class Trimester {
    First,
    Second,
    Third,
    PostDue,
}

data class PregnancyProgress(
    val lmpDate: LocalDate,
    val dueDate: LocalDate,
    val gestationalDays: Long,
    val week: Long,
    val day: Long,
    val daysUntilDueDate: Long,
    val trimester: Trimester,
)

object PregnancyCalculator {
    fun fromLastMenstrualPeriod(
        lmpDate: LocalDate,
        today: LocalDate,
    ): PregnancyProgress {
        val dueDate = lmpDate.plusDays(FULL_TERM_DAYS)
        return buildProgress(lmpDate = lmpDate, dueDate = dueDate, today = today)
    }

    fun fromDueDate(
        dueDate: LocalDate,
        today: LocalDate,
    ): PregnancyProgress {
        val lmpDate = dueDate.minusDays(FULL_TERM_DAYS)
        return buildProgress(lmpDate = lmpDate, dueDate = dueDate, today = today)
    }

    fun fromCurrentGestationalAge(
        week: Int,
        day: Int,
        setupDate: LocalDate,
        today: LocalDate,
    ): PregnancyProgress {
        require(week >= 0) { "week must be greater than or equal to 0" }
        require(day in 0..6) { "day must be in 0..6" }

        val gestationalDaysAtSetup = week * 7L + day
        val lmpDate = setupDate.minusDays(gestationalDaysAtSetup)
        val dueDate = lmpDate.plusDays(FULL_TERM_DAYS)
        return buildProgress(lmpDate = lmpDate, dueDate = dueDate, today = today)
    }

    private fun buildProgress(
        lmpDate: LocalDate,
        dueDate: LocalDate,
        today: LocalDate,
    ): PregnancyProgress {
        val gestationalDays = ChronoUnit.DAYS.between(lmpDate, today).coerceAtLeast(0)
        val week = gestationalDays / 7
        val day = gestationalDays % 7
        val daysUntilDueDate = ChronoUnit.DAYS.between(today, dueDate)

        return PregnancyProgress(
            lmpDate = lmpDate,
            dueDate = dueDate,
            gestationalDays = gestationalDays,
            week = week,
            day = day,
            daysUntilDueDate = daysUntilDueDate,
            trimester = trimesterFor(week),
        )
    }

    private fun trimesterFor(week: Long): Trimester = when {
        week <= 13 -> Trimester.First
        week <= 27 -> Trimester.Second
        week <= 42 -> Trimester.Third
        else -> Trimester.PostDue
    }
}
