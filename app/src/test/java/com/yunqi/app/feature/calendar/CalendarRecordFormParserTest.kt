package com.yunqi.app.feature.calendar

import com.yunqi.app.domain.calendar.CalendarRecordType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CalendarRecordFormParserTest {
    private val parser = CalendarRecordFormParser(
        idProvider = { "record-1" },
        nowProvider = { 1000L },
    )

    @Test
    fun `parses appointment record`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.Appointment,
                note = "Bring report",
                weightKg = "",
                fetalMovementCount = "",
                appointmentTime = "09:30",
                appointmentLocation = "Clinic",
                appointmentDoctor = "  Dr Chen  ",
                appointmentItems = "Blood test",
                appointmentResult = "Normal",
            ),
        )

        assertTrue(result is CalendarRecordParseResult.Success)
        val record = (result as CalendarRecordParseResult.Success).record
        assertEquals("record-1", record.id)
        assertEquals(LocalDate.of(2026, 6, 21), record.date)
        assertEquals(CalendarRecordType.Appointment, record.type)
        assertEquals("09:30", record.appointmentTime)
        assertEquals("Clinic", record.appointmentLocation)
        assertEquals("Dr Chen", record.appointmentDoctor)
        assertEquals("Blood test", record.appointmentItems)
        assertEquals("Normal", record.appointmentResult)
    }

    @Test
    fun `allows appointment record without time`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.Appointment,
                note = "",
                weightKg = "",
                fetalMovementCount = "",
                appointmentTime = "",
                appointmentLocation = "Clinic",
            ),
        )

        val record = (result as CalendarRecordParseResult.Success).record
        assertNull(record.appointmentTime)
        assertEquals("Clinic", record.appointmentLocation)
    }

    @Test
    fun `rejects invalid appointment time`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.Appointment,
                note = "",
                weightKg = "",
                fetalMovementCount = "",
                appointmentTime = "9:30",
                appointmentLocation = "Clinic",
            ),
        )

        assertEquals(CalendarRecordParseResult.InvalidAppointmentTime, result)
    }

    @Test
    fun `parses weight record`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.Weight,
                note = "",
                weightKg = "56.5",
                fetalMovementCount = "",
                appointmentTime = "",
                appointmentLocation = "",
            ),
        )

        val record = (result as CalendarRecordParseResult.Success).record
        assertEquals(56.5, record.weightKg ?: 0.0, 0.001)
        assertNull(record.appointmentDoctor)
        assertNull(record.appointmentItems)
        assertNull(record.appointmentResult)
    }

    @Test
    fun `rejects invalid weight`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.Weight,
                note = "",
                weightKg = "0",
                fetalMovementCount = "",
                appointmentTime = "",
                appointmentLocation = "",
            ),
        )

        assertEquals(CalendarRecordParseResult.InvalidWeight, result)
    }

    @Test
    fun `parses fetal movement record`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.FetalMovement,
                note = "",
                weightKg = "",
                fetalMovementCount = "12",
                fetalMovementPeriod = "  evening  ",
                fetalMovementFeeling = "Active after dinner",
                appointmentTime = "",
                appointmentLocation = "",
            ),
        )

        val record = (result as CalendarRecordParseResult.Success).record
        assertEquals(12, record.fetalMovementCount)
        assertEquals("evening", record.fetalMovementPeriod)
        assertEquals("Active after dinner", record.fetalMovementFeeling)
    }

    @Test
    fun `rejects invalid fetal movement count`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.FetalMovement,
                note = "",
                weightKg = "",
                fetalMovementCount = "-1",
                appointmentTime = "",
                appointmentLocation = "",
            ),
        )

        assertEquals(CalendarRecordParseResult.InvalidFetalMovement, result)
    }

    @Test
    fun `parses exercise record`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.Exercise,
                note = "Walk",
                weightKg = "",
                fetalMovementCount = "",
                appointmentTime = "",
                appointmentLocation = "",
                exerciseType = "  Walking  ",
                exerciseMinutes = "30",
                exerciseIntensity = "Low",
            ),
        )

        val record = (result as CalendarRecordParseResult.Success).record
        assertEquals("Walking", record.exerciseType)
        assertEquals(30, record.exerciseMinutes)
        assertEquals("Low", record.exerciseIntensity)
    }

    @Test
    fun `parses symptom record`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.Symptom,
                note = "Called doctor",
                weightKg = "",
                fetalMovementCount = "",
                symptomType = "  headache  ",
                symptomSeverity = "mild",
                appointmentTime = "",
                appointmentLocation = "",
            ),
        )

        val record = (result as CalendarRecordParseResult.Success).record
        assertEquals("headache", record.symptomType)
        assertEquals("mild", record.symptomSeverity)
        assertEquals("Called doctor", record.note)
    }

    @Test
    fun `parses diet record`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.Diet,
                note = "",
                weightKg = "",
                fetalMovementCount = "",
                dietMeal = " lunch ",
                dietContent = "Rice, fish, vegetables",
                appointmentTime = "",
                appointmentLocation = "",
            ),
        )

        val record = (result as CalendarRecordParseResult.Success).record
        assertEquals("lunch", record.dietMeal)
        assertEquals("Rice, fish, vegetables", record.dietContent)
    }

    @Test
    fun `rejects invalid exercise minutes`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026-06-21",
                type = CalendarRecordType.Exercise,
                note = "",
                weightKg = "",
                fetalMovementCount = "",
                appointmentTime = "",
                appointmentLocation = "",
                exerciseMinutes = "0",
            ),
        )

        assertEquals(CalendarRecordParseResult.InvalidExerciseMinutes, result)
    }

    @Test
    fun `rejects invalid date`() {
        val result = parser.parse(
            CalendarRecordInput(
                date = "2026/06/21",
                type = CalendarRecordType.Note,
                note = "",
                weightKg = "",
                fetalMovementCount = "",
                appointmentTime = "",
                appointmentLocation = "",
            ),
        )

        assertEquals(CalendarRecordParseResult.InvalidDate, result)
    }

    @Test
    fun `preserves id and creation time when editing record`() {
        val result = parser.parse(
            CalendarRecordInput(
                id = "existing-record",
                date = "2026-06-21",
                type = CalendarRecordType.Note,
                note = "Updated note",
                weightKg = "",
                fetalMovementCount = "",
                appointmentTime = "",
                appointmentLocation = "",
                createdAtEpochMillis = 42L,
            ),
        )

        val record = (result as CalendarRecordParseResult.Success).record
        assertEquals("existing-record", record.id)
        assertEquals(42L, record.createdAtEpochMillis)
        assertEquals("Updated note", record.note)
    }
}
