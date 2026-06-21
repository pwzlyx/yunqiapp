package com.yunqi.app.domain.pregnancy

import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val FULL_TERM_DAYS = 280L
private const val CONCEPTION_TO_DUE_DATE_DAYS = 266L
private const val LMP_TO_CONCEPTION_DAYS = 14L

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
    /**
     * Estimates pregnancy progress from the first day of the last menstrual period.
     */
    fun fromLastMenstrualPeriod(
        lmpDate: LocalDate,
        today: LocalDate,
    ): PregnancyProgress {
        val dueDate = lmpDate.plusDays(FULL_TERM_DAYS)
        return buildProgress(lmpDate = lmpDate, dueDate = dueDate, today = today)
    }

    /**
     * Estimates pregnancy progress by reversing the standard 280-day due-date rule.
     */
    fun fromDueDate(
        dueDate: LocalDate,
        today: LocalDate,
    ): PregnancyProgress {
        val lmpDate = dueDate.minusDays(FULL_TERM_DAYS)
        return buildProgress(lmpDate = lmpDate, dueDate = dueDate, today = today)
    }

    /**
     * Estimates pregnancy progress from an entered conception date.
     */
    fun fromConceptionDate(
        conceptionDate: LocalDate,
        today: LocalDate,
    ): PregnancyProgress {
        val lmpDate = conceptionDate.minusDays(LMP_TO_CONCEPTION_DAYS)
        val dueDate = conceptionDate.plusDays(CONCEPTION_TO_DUE_DATE_DAYS)
        return buildProgress(lmpDate = lmpDate, dueDate = dueDate, today = today)
    }

    /**
     * Estimates pregnancy progress from a user-entered gestational age captured on setup day.
     */
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

    /**
     * Builds the normalized progress object shared by all supported calculation methods.
     */
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

    /**
     * Maps gestational week to the app's broad pregnancy stage labels.
     */
    private fun trimesterFor(week: Long): Trimester = when {
        week <= 13 -> Trimester.First
        week <= 27 -> Trimester.Second
        week <= 42 -> Trimester.Third
        else -> Trimester.PostDue
    }
}
