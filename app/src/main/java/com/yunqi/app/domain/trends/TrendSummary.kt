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
    val exerciseRecordCount: Int,
    val latestExerciseMinutes: Int?,
    val totalExerciseMinutes: Int,
    val exercisePoints: List<TrendPoint>,
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
        val exerciseRecords = records
            .filter { it.type == CalendarRecordType.Exercise && it.exerciseMinutes != null }
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
        val exercisePoints = exerciseRecords.map { record ->
            TrendPoint(
                date = record.date,
                value = (record.exerciseMinutes ?: 0).toDouble(),
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
            exerciseRecordCount = exerciseRecords.size,
            latestExerciseMinutes = exerciseRecords.lastOrNull()?.exerciseMinutes,
            totalExerciseMinutes = exerciseRecords.mapNotNull(CalendarRecord::exerciseMinutes).sum(),
            exercisePoints = exercisePoints,
        )
    }

    private fun List<CalendarRecord>.calculateWeightChange(): Double? {
        val first = firstOrNull()?.weightKg ?: return null
        val latest = lastOrNull()?.weightKg ?: return null
        return latest - first
    }
}

enum class TrendRange(val days: Long?) {
    Last7Days(days = 7),
    Last30Days(days = 30),
    All(days = null),
}

object TrendRecordFilter {
    /**
     * Keeps records inside the selected inclusive trend range.
     */
    fun filter(records: List<CalendarRecord>, range: TrendRange, today: LocalDate): List<CalendarRecord> {
        val days = range.days ?: return records
        val startDate = today.minusDays(days - 1)
        return records.filter { record -> record.date in startDate..today }
    }
}

object TrendChartPolicy {
    /**
     * Shows a trend chart only when there are enough points to compare change over time.
     */
    fun shouldShowChart(points: List<TrendPoint>): Boolean = points.size >= 2
}
