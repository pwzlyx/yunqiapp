package com.yunqi.app.domain.trends

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class TrendSummaryCalculatorTest {
    @Test
    fun `calculates empty summary`() {
        val summary = TrendSummaryCalculator.calculate(emptyList())

        assertEquals(0, summary.weightRecordCount)
        assertNull(summary.latestWeightKg)
        assertNull(summary.weightChangeKg)
        assertEquals(emptyList<TrendPoint>(), summary.weightPoints)
        assertEquals(0, summary.fetalMovementRecordCount)
        assertNull(summary.latestFetalMovementCount)
        assertNull(summary.averageFetalMovementCount)
        assertEquals(emptyList<TrendPoint>(), summary.fetalMovementPoints)
        assertEquals(0, summary.exerciseRecordCount)
        assertNull(summary.latestExerciseMinutes)
        assertEquals(0, summary.totalExerciseMinutes)
        assertEquals(emptyList<TrendPoint>(), summary.exercisePoints)
    }

    @Test
    fun `calculates trend summaries and points`() {
        val records = listOf(
            record(
                id = "weight-old",
                date = LocalDate.of(2026, 6, 1),
                type = CalendarRecordType.Weight,
                weightKg = 55.0,
            ),
            record(
                id = "weight-new",
                date = LocalDate.of(2026, 6, 5),
                type = CalendarRecordType.Weight,
                weightKg = 56.2,
            ),
            record(
                id = "movement",
                date = LocalDate.of(2026, 6, 3),
                type = CalendarRecordType.FetalMovement,
                fetalMovementCount = 12,
            ),
            record(
                id = "movement-new",
                date = LocalDate.of(2026, 6, 4),
                type = CalendarRecordType.FetalMovement,
                fetalMovementCount = 16,
            ),
            record(
                id = "exercise",
                date = LocalDate.of(2026, 6, 6),
                type = CalendarRecordType.Exercise,
                exerciseMinutes = 30,
            ),
        )

        val summary = TrendSummaryCalculator.calculate(records)

        assertEquals(2, summary.weightRecordCount)
        assertEquals(56.2, summary.latestWeightKg ?: 0.0, 0.001)
        assertEquals(1.2, summary.weightChangeKg ?: 0.0, 0.001)
        assertEquals(
            listOf(
                TrendPoint(LocalDate.of(2026, 6, 1), 55.0),
                TrendPoint(LocalDate.of(2026, 6, 5), 56.2),
            ),
            summary.weightPoints,
        )
        assertEquals(2, summary.fetalMovementRecordCount)
        assertEquals(16, summary.latestFetalMovementCount)
        assertEquals(14.0, summary.averageFetalMovementCount ?: 0.0, 0.001)
        assertEquals(
            listOf(
                TrendPoint(LocalDate.of(2026, 6, 3), 12.0),
                TrendPoint(LocalDate.of(2026, 6, 4), 16.0),
            ),
            summary.fetalMovementPoints,
        )
        assertEquals(1, summary.exerciseRecordCount)
        assertEquals(30, summary.latestExerciseMinutes)
        assertEquals(30, summary.totalExerciseMinutes)
        assertEquals(
            listOf(TrendPoint(LocalDate.of(2026, 6, 6), 30.0)),
            summary.exercisePoints,
        )
    }

    private fun record(
        id: String,
        date: LocalDate,
        type: CalendarRecordType,
        weightKg: Double? = null,
        fetalMovementCount: Int? = null,
        exerciseMinutes: Int? = null,
    ): CalendarRecord = CalendarRecord(
        id = id,
        date = date,
        type = type,
        note = "",
        weightKg = weightKg,
        fetalMovementCount = fetalMovementCount,
        exerciseMinutes = exerciseMinutes,
        appointmentTime = null,
        appointmentLocation = null,
        appointmentDoctor = null,
        appointmentItems = null,
        appointmentResult = null,
        createdAtEpochMillis = 0L,
    )
}
