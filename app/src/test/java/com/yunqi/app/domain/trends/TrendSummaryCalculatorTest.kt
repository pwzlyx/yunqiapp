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
        assertEquals(0, summary.fetalMovementRecordCount)
        assertNull(summary.latestFetalMovementCount)
    }

    @Test
    fun `calculates latest weight and fetal movement`() {
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
        )

        val summary = TrendSummaryCalculator.calculate(records)

        assertEquals(2, summary.weightRecordCount)
        assertEquals(56.2, summary.latestWeightKg ?: 0.0, 0.001)
        assertEquals(1, summary.fetalMovementRecordCount)
        assertEquals(12, summary.latestFetalMovementCount)
    }

    private fun record(
        id: String,
        date: LocalDate,
        type: CalendarRecordType,
        weightKg: Double? = null,
        fetalMovementCount: Int? = null,
    ): CalendarRecord = CalendarRecord(
        id = id,
        date = date,
        type = type,
        note = "",
        weightKg = weightKg,
        fetalMovementCount = fetalMovementCount,
        appointmentTime = null,
        appointmentLocation = null,
        createdAtEpochMillis = 0L,
    )
}

