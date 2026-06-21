package com.yunqi.app.domain.trends

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.LocalDate

data class TrendSummary(
    val weightRecordCount: Int,
    val latestWeightKg: Double?,
    val weightChangeKg: Double?,
    val weightPoints: List<TrendPoint>,
    val fetalMovementRecordCount: Int,
    val latestFetalMovementCount: Int?,
    val averageFetalMovementCount: Double?,
    val fetalMovementPoints: List<TrendPoint>,
)

data class TrendPoint(
    val date: LocalDate,
    val value: Double,
)

object TrendSummaryCalculator {
    /**
     * Aggregates local calendar records into the current trends dashboard summary.
     */
    fun calculate(records: List<CalendarRecord>): TrendSummary {
        val weightRecords = records
            .filter { it.type == CalendarRecordType.Weight && it.weightKg != null }
            .sortedWith(compareBy<CalendarRecord> { it.date }.thenBy { it.createdAtEpochMillis })
        val fetalMovementRecords = records
            .filter { it.type == CalendarRecordType.FetalMovement && it.fetalMovementCount != null }
            .sortedWith(compareBy<CalendarRecord> { it.date }.thenBy { it.createdAtEpochMillis })
        val weightPoints = weightRecords.map { record ->
            TrendPoint(
                date = record.date,
                value = record.weightKg ?: 0.0,
            )
        }
        val fetalMovementPoints = fetalMovementRecords.map { record ->
            TrendPoint(
                date = record.date,
                value = (record.fetalMovementCount ?: 0).toDouble(),
            )
        }

        return TrendSummary(
            weightRecordCount = weightRecords.size,
            latestWeightKg = weightRecords.lastOrNull()?.weightKg,
            weightChangeKg = weightRecords.calculateWeightChange(),
            weightPoints = weightPoints,
            fetalMovementRecordCount = fetalMovementRecords.size,
            latestFetalMovementCount = fetalMovementRecords.lastOrNull()?.fetalMovementCount,
            averageFetalMovementCount = fetalMovementRecords
                .takeIf { it.isNotEmpty() }
                ?.mapNotNull(CalendarRecord::fetalMovementCount)
                ?.average(),
            fetalMovementPoints = fetalMovementPoints,
        )
    }

    private fun List<CalendarRecord>.calculateWeightChange(): Double? {
        val first = firstOrNull()?.weightKg ?: return null
        val latest = lastOrNull()?.weightKg ?: return null
        return latest - first
    }
}
