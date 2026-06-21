package com.yunqi.app.domain.trends

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType

data class TrendSummary(
    val weightRecordCount: Int,
    val latestWeightKg: Double?,
    val fetalMovementRecordCount: Int,
    val latestFetalMovementCount: Int?,
)

object TrendSummaryCalculator {
    /**
     * Aggregates local calendar records into the current trends dashboard summary.
     */
    fun calculate(records: List<CalendarRecord>): TrendSummary {
        val weightRecords = records
            .filter { it.type == CalendarRecordType.Weight && it.weightKg != null }
            .sortedByDescending { it.date }
        val fetalMovementRecords = records
            .filter { it.type == CalendarRecordType.FetalMovement && it.fetalMovementCount != null }
            .sortedByDescending { it.date }

        return TrendSummary(
            weightRecordCount = weightRecords.size,
            latestWeightKg = weightRecords.firstOrNull()?.weightKg,
            fetalMovementRecordCount = fetalMovementRecords.size,
            latestFetalMovementCount = fetalMovementRecords.firstOrNull()?.fetalMovementCount,
        )
    }
}

