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
    val appointmentPlans: List<AppointmentPlan>,
)

data class TrendPoint(
    val date: LocalDate,
    val value: Double,
)

data class AppointmentPlan(
    val date: LocalDate,
    val time: String?,
    val location: String?,
    val doctor: String?,
    val items: String?,
)

object TrendSummaryCalculator {
    /**
     * Aggregates local calendar records into the current trends dashboard summary.
     */
    fun calculate(
        records: List<CalendarRecord>,
        appointmentRecords: List<CalendarRecord> = records,
        today: LocalDate? = null,
    ): TrendSummary {
        val weightRecords = records
            .filter { it.type == CalendarRecordType.Weight && it.weightKg != null }
            .sortedWith(compareBy<CalendarRecord> { it.date }.thenBy { it.createdAtEpochMillis })
        val fetalMovementRecords = records
            .filter { it.type == CalendarRecordType.FetalMovement && it.fetalMovementCount != null }
            .sortedWith(compareBy<CalendarRecord> { it.date }.thenBy { it.createdAtEpochMillis })
        val exerciseRecords = records
            .filter { it.type == CalendarRecordType.Exercise && it.exerciseMinutes != null }
            .sortedWith(compareBy<CalendarRecord> { it.date }.thenBy { it.createdAtEpochMillis })
        val appointmentPlans = appointmentRecords
            .filter { it.type == CalendarRecordType.Appointment }
            .filter { record -> today == null || record.date >= today }
            .sortedWith(compareBy<CalendarRecord> { it.date }.thenBy { it.appointmentTime.orEmpty().trim() })
            .map { record ->
                AppointmentPlan(
                    date = record.date,
                    time = record.appointmentTime.trimmedOrNull(),
                    location = record.appointmentLocation.trimmedOrNull(),
                    doctor = record.appointmentDoctor.trimmedOrNull(),
                    items = record.appointmentItems.trimmedOrNull(),
                )
            }
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
            appointmentPlans = appointmentPlans,
        )
    }

    private fun List<CalendarRecord>.calculateWeightChange(): Double? {
        val first = firstOrNull()?.weightKg ?: return null
        val latest = lastOrNull()?.weightKg ?: return null
        return latest - first
    }
}

private fun String?.trimmedOrNull(): String? = this
    ?.trim()
    ?.takeIf(String::isNotBlank)

enum class TrendRange(val days: Long?) {
    Last7Days(days = 7),
    Last30Days(days = 30),
    All(days = null),
}

object TrendRecordFilter {
    /**
     * Keeps metric records inside the selected inclusive trend range up to today.
     */
    fun filter(records: List<CalendarRecord>, range: TrendRange, today: LocalDate): List<CalendarRecord> {
        val recordsUntilToday = records.filter { record -> record.date <= today }
        val days = range.days ?: return recordsUntilToday
        val startDate = today.minusDays(days - 1)
        return recordsUntilToday.filter { record -> record.date in startDate..today }
    }
}

object TrendChartPolicy {
    /**
     * Shows a trend chart only when there are enough points to compare change over time.
     */
    fun shouldShowChart(points: List<TrendPoint>): Boolean = points.size >= 2
}
