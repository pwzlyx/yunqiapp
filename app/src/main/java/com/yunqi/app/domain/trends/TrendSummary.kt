package com.yunqi.app.domain.trends

import com.yunqi.app.core.time.isStrictHourMinute
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
            .filter { it.type == CalendarRecordType.Weight && it.weightKg.isSupportedTrendWeightKg() }
            .sortedWith(compareBy<CalendarRecord> { it.date }.thenBy { it.createdAtEpochMillis })
        val fetalMovementRecords = records
            .filter {
                it.type == CalendarRecordType.FetalMovement &&
                    it.fetalMovementCount.isSupportedTrendFetalMovementCount()
            }
            .sortedWith(compareBy<CalendarRecord> { it.date }.thenBy { it.createdAtEpochMillis })
        val exerciseRecords = records
            .filter {
                it.type == CalendarRecordType.Exercise &&
                    it.exerciseMinutes.isSupportedTrendExerciseMinutes()
            }
            .sortedWith(compareBy<CalendarRecord> { it.date }.thenBy { it.createdAtEpochMillis })
        val appointmentPlans = appointmentRecords
            .filter { it.type == CalendarRecordType.Appointment }
            .filter { record -> today == null || record.date >= today }
            .sortedWith(
                compareBy<CalendarRecord> { it.date }
                    .thenBy { it.appointmentTime.toAppointmentSortTime() ?: LAST_APPOINTMENT_SORT_TIME }
                    .thenBy { it.createdAtEpochMillis },
            )
            .map { record ->
                AppointmentPlan(
                    date = record.date,
                    time = record.appointmentTime.trimmedOrNull(),
                    location = record.appointmentLocation.toAppointmentPlanDisplayTextOrNull(),
                    doctor = record.appointmentDoctor.toAppointmentPlanDisplayTextOrNull(),
                    items = record.appointmentItems.toAppointmentPlanDisplayTextOrNull(),
                )
            }
        val weightPoints = weightRecords.toLatestDailyPoints { record -> record.weightKg }
        val fetalMovementPoints = fetalMovementRecords.toDailySumPoints { record ->
            record.fetalMovementCount?.toDouble()
        }
        val exercisePoints = exerciseRecords.toDailySumPoints { record ->
            record.exerciseMinutes?.toDouble()
        }

        return TrendSummary(
            weightRecordCount = weightRecords.size,
            latestWeightKg = weightRecords.lastOrNull()?.weightKg,
            weightChangeKg = weightPoints.calculateValueChange(),
            weightPoints = weightPoints,
            fetalMovementRecordCount = fetalMovementRecords.size,
            latestFetalMovementCount = fetalMovementPoints.lastOrNull()?.value?.toInt(),
            averageFetalMovementCount = fetalMovementPoints
                .takeIf { it.isNotEmpty() }
                ?.map(TrendPoint::value)
                ?.average(),
            fetalMovementPoints = fetalMovementPoints,
            exerciseRecordCount = exerciseRecords.size,
            latestExerciseMinutes = exercisePoints.lastOrNull()?.value?.toInt(),
            totalExerciseMinutes = exerciseRecords.mapNotNull(CalendarRecord::exerciseMinutes).sum(),
            exercisePoints = exercisePoints,
            appointmentPlans = appointmentPlans,
        )
    }

    private fun List<TrendPoint>.calculateValueChange(): Double? {
        val first = firstOrNull()?.value ?: return null
        val latest = lastOrNull()?.value ?: return null
        return latest - first
    }
}

/**
 * Uses the latest record for each day so same-day weight corrections do not duplicate chart points.
 */
private fun List<CalendarRecord>.toLatestDailyPoints(valueProvider: (CalendarRecord) -> Double?): List<TrendPoint> =
    groupBy(CalendarRecord::date)
        .mapNotNull { (date, records) ->
            records.asReversed().mapNotNull(valueProvider).firstOrNull()?.let { value ->
                TrendPoint(date = date, value = value)
            }
        }
        .sortedBy(TrendPoint::date)

/**
 * Sums same-day entries so repeated fetal movement or exercise sessions render as one daily point.
 */
private fun List<CalendarRecord>.toDailySumPoints(valueProvider: (CalendarRecord) -> Double?): List<TrendPoint> =
    groupBy(CalendarRecord::date)
        .mapNotNull { (date, records) ->
            val values = records.mapNotNull(valueProvider)
            values.takeIf { it.isNotEmpty() }?.let {
                TrendPoint(date = date, value = it.sum())
            }
        }
        .sortedBy(TrendPoint::date)

private fun String?.trimmedOrNull(): String? = this
    ?.trim()
    ?.takeIf(String::isNotBlank)

private fun String?.toAppointmentPlanDisplayTextOrNull(): String? = this
    ?.trim()
    ?.take(MAX_APPOINTMENT_PLAN_DISPLAY_TEXT_LENGTH)
    ?.takeIf(String::isNotBlank)

private const val LAST_APPOINTMENT_SORT_TIME = "99:99"

private fun String?.toAppointmentSortTime(): String? =
    this?.trim()?.takeIf(String::isStrictHourMinute)

private fun Double?.isSupportedTrendWeightKg(): Boolean =
    this != null && this > 0.0 && this <= MAX_TREND_WEIGHT_KG

private fun Int?.isSupportedTrendFetalMovementCount(): Boolean =
    this != null && this in 0..MAX_TREND_FETAL_MOVEMENT_COUNT

private fun Int?.isSupportedTrendExerciseMinutes(): Boolean =
    this != null && this in 1..MAX_TREND_EXERCISE_MINUTES_PER_DAY

private const val MAX_TREND_WEIGHT_KG = 300.0
private const val MAX_TREND_FETAL_MOVEMENT_COUNT = 1_000
private const val MAX_TREND_EXERCISE_MINUTES_PER_DAY = 24 * 60
internal const val MAX_APPOINTMENT_PLAN_DISPLAY_TEXT_LENGTH = 180

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
