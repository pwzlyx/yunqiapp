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
        assertEquals(emptyList<AppointmentPlan>(), summary.appointmentPlans)
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
            record(
                id = "appointment-later",
                date = LocalDate.of(2026, 6, 10),
                type = CalendarRecordType.Appointment,
                appointmentTime = "14:00",
                appointmentLocation = "Clinic B",
                appointmentDoctor = "Dr Li",
                appointmentItems = "Ultrasound",
            ),
            record(
                id = "appointment-earlier",
                date = LocalDate.of(2026, 6, 8),
                type = CalendarRecordType.Appointment,
                appointmentTime = "09:30",
                appointmentLocation = "Clinic A",
                appointmentDoctor = "Dr Chen",
                appointmentItems = "Blood test",
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
        assertEquals(
            listOf(
                AppointmentPlan(
                    date = LocalDate.of(2026, 6, 8),
                    time = "09:30",
                    location = "Clinic A",
                    doctor = "Dr Chen",
                    items = "Blood test",
                ),
                AppointmentPlan(
                    date = LocalDate.of(2026, 6, 10),
                    time = "14:00",
                    location = "Clinic B",
                    doctor = "Dr Li",
                    items = "Ultrasound",
                ),
            ),
            summary.appointmentPlans,
        )
    }

    @Test
    fun `shows upcoming appointment plans from all records independent of trend range records`() {
        val trendRecords = listOf(
            record(
                id = "weight",
                date = LocalDate.of(2026, 6, 21),
                type = CalendarRecordType.Weight,
                weightKg = 56.2,
            ),
        )
        val appointmentRecords = listOf(
            record(
                id = "past-appointment",
                date = LocalDate.of(2026, 6, 20),
                type = CalendarRecordType.Appointment,
                appointmentTime = "09:00",
                appointmentLocation = "Past Clinic",
            ),
            record(
                id = "future-later",
                date = LocalDate.of(2026, 7, 20),
                type = CalendarRecordType.Appointment,
                appointmentTime = "14:00",
                appointmentLocation = "Clinic B",
            ),
            record(
                id = "future-sooner",
                date = LocalDate.of(2026, 6, 30),
                type = CalendarRecordType.Appointment,
                appointmentTime = "10:00",
                appointmentLocation = "Clinic A",
            ),
        )

        val summary = TrendSummaryCalculator.calculate(
            records = trendRecords,
            appointmentRecords = appointmentRecords,
            today = LocalDate.of(2026, 6, 21),
        )

        assertEquals(
            listOf(
                AppointmentPlan(
                    date = LocalDate.of(2026, 6, 30),
                    time = "10:00",
                    location = "Clinic A",
                    doctor = null,
                    items = null,
                ),
                AppointmentPlan(
                    date = LocalDate.of(2026, 7, 20),
                    time = "14:00",
                    location = "Clinic B",
                    doctor = null,
                    items = null,
                ),
            ),
            summary.appointmentPlans,
        )
        assertEquals(1, summary.weightRecordCount)
    }

    @Test
    fun `aggregates same day metric records into daily trend points`() {
        val summary = TrendSummaryCalculator.calculate(
            records = listOf(
                record(
                    id = "weight-morning",
                    date = LocalDate.of(2026, 6, 21),
                    type = CalendarRecordType.Weight,
                    weightKg = 56.0,
                    createdAtEpochMillis = 1L,
                ),
                record(
                    id = "weight-evening",
                    date = LocalDate.of(2026, 6, 21),
                    type = CalendarRecordType.Weight,
                    weightKg = 56.4,
                    createdAtEpochMillis = 2L,
                ),
                record(
                    id = "movement-morning",
                    date = LocalDate.of(2026, 6, 21),
                    type = CalendarRecordType.FetalMovement,
                    fetalMovementCount = 8,
                    createdAtEpochMillis = 3L,
                ),
                record(
                    id = "movement-evening",
                    date = LocalDate.of(2026, 6, 21),
                    type = CalendarRecordType.FetalMovement,
                    fetalMovementCount = 12,
                    createdAtEpochMillis = 4L,
                ),
                record(
                    id = "exercise-walk",
                    date = LocalDate.of(2026, 6, 21),
                    type = CalendarRecordType.Exercise,
                    exerciseMinutes = 20,
                    createdAtEpochMillis = 5L,
                ),
                record(
                    id = "exercise-swim",
                    date = LocalDate.of(2026, 6, 21),
                    type = CalendarRecordType.Exercise,
                    exerciseMinutes = 15,
                    createdAtEpochMillis = 6L,
                ),
            ),
        )

        assertEquals(2, summary.weightRecordCount)
        assertEquals(listOf(TrendPoint(LocalDate.of(2026, 6, 21), 56.4)), summary.weightPoints)
        assertEquals(2, summary.fetalMovementRecordCount)
        assertEquals(20, summary.latestFetalMovementCount)
        assertEquals(listOf(TrendPoint(LocalDate.of(2026, 6, 21), 20.0)), summary.fetalMovementPoints)
        assertEquals(20.0, summary.averageFetalMovementCount ?: 0.0, 0.001)
        assertEquals(2, summary.exerciseRecordCount)
        assertEquals(35, summary.latestExerciseMinutes)
        assertEquals(listOf(TrendPoint(LocalDate.of(2026, 6, 21), 35.0)), summary.exercisePoints)
        assertEquals(35, summary.totalExerciseMinutes)
    }

    @Test
    fun `trims appointment plan fields from stored records`() {
        val summary = TrendSummaryCalculator.calculate(
            records = listOf(
                record(
                    id = "future",
                    date = LocalDate.of(2026, 6, 30),
                    type = CalendarRecordType.Appointment,
                    appointmentTime = " 10:00 ",
                    appointmentLocation = "  Clinic A  ",
                    appointmentDoctor = "   ",
                    appointmentItems = " Ultrasound ",
                ),
            ),
        )

        assertEquals(
            listOf(
                AppointmentPlan(
                    date = LocalDate.of(2026, 6, 30),
                    time = "10:00",
                    location = "Clinic A",
                    doctor = null,
                    items = "Ultrasound",
                ),
            ),
            summary.appointmentPlans,
        )
    }

    @Test
    fun `sorts appointment plan fields by trimmed time`() {
        val summary = TrendSummaryCalculator.calculate(
            records = listOf(
                record(
                    id = "afternoon",
                    date = LocalDate.of(2026, 6, 30),
                    type = CalendarRecordType.Appointment,
                    appointmentTime = " 14:00 ",
                    appointmentLocation = "Clinic B",
                ),
                record(
                    id = "morning",
                    date = LocalDate.of(2026, 6, 30),
                    type = CalendarRecordType.Appointment,
                    appointmentTime = "09:00",
                    appointmentLocation = "Clinic A",
                ),
            ),
        )

        assertEquals("09:00", summary.appointmentPlans.first().time)
    }

    @Test
    fun `puts untimed appointment plans after timed plans on the same day`() {
        val summary = TrendSummaryCalculator.calculate(
            records = listOf(
                record(
                    id = "untimed",
                    date = LocalDate.of(2026, 6, 30),
                    type = CalendarRecordType.Appointment,
                    appointmentTime = "",
                    appointmentLocation = "Clinic B",
                    createdAtEpochMillis = 1L,
                ),
                record(
                    id = "timed",
                    date = LocalDate.of(2026, 6, 30),
                    type = CalendarRecordType.Appointment,
                    appointmentTime = "09:00",
                    appointmentLocation = "Clinic A",
                    createdAtEpochMillis = 2L,
                ),
            ),
        )

        assertEquals("09:00", summary.appointmentPlans.first().time)
        assertEquals("Clinic B", summary.appointmentPlans.last().location)
    }

    private fun record(
        id: String,
        date: LocalDate,
        type: CalendarRecordType,
        weightKg: Double? = null,
        fetalMovementCount: Int? = null,
        exerciseMinutes: Int? = null,
        appointmentTime: String? = null,
        appointmentLocation: String? = null,
        appointmentDoctor: String? = null,
        appointmentItems: String? = null,
        createdAtEpochMillis: Long = 0L,
    ): CalendarRecord = CalendarRecord(
        id = id,
        date = date,
        type = type,
        note = "",
        weightKg = weightKg,
        fetalMovementCount = fetalMovementCount,
        fetalMovementPeriod = null,
        fetalMovementFeeling = null,
        exerciseMinutes = exerciseMinutes,
        appointmentTime = appointmentTime,
        appointmentLocation = appointmentLocation,
        appointmentDoctor = appointmentDoctor,
        appointmentItems = appointmentItems,
        appointmentResult = null,
        createdAtEpochMillis = createdAtEpochMillis,
    )
}
