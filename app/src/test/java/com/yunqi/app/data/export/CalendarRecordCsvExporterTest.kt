package com.yunqi.app.data.export

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class CalendarRecordCsvExporterTest {
    private val exporter = CalendarRecordCsvExporter()

    @Test
    fun `exports header when there are no records`() {
        val csv = exporter.export(emptyList())

        assertEquals(
            "\uFEFFid,date,type,weightKg,fetalMovementCount,fetalMovementPeriod,fetalMovementFeeling," +
                "symptomType,symptomSeverity,exerciseType,exerciseMinutes,exerciseIntensity,dietMeal,dietContent,appointmentTime," +
                "appointmentLocation,appointmentDoctor,appointmentItems,appointmentResult,note,createdAtEpochMillis\n",
            csv,
        )
    }

    @Test
    fun `escapes commas quotes and line breaks`() {
        val csv = exporter.export(
            listOf(
                CalendarRecord(
                    id = "record-1",
                    date = LocalDate.of(2026, 6, 21),
                    type = CalendarRecordType.Appointment,
                    note = "Bring \"old\" report\nand water",
                    weightKg = null,
                    fetalMovementCount = null,
                    fetalMovementPeriod = null,
                    fetalMovementFeeling = null,
                    symptomType = null,
                    symptomSeverity = null,
                    exerciseType = null,
                    exerciseMinutes = null,
                    exerciseIntensity = null,
                    dietMeal = null,
                    dietContent = null,
                    appointmentTime = "09:30",
                    appointmentLocation = "Clinic, Room 2",
                    appointmentDoctor = "Dr Chen",
                    appointmentItems = "Blood test",
                    appointmentResult = "Normal",
                    createdAtEpochMillis = 42L,
                ),
            ),
        )

        assertEquals(
            "\uFEFFid,date,type,weightKg,fetalMovementCount,fetalMovementPeriod,fetalMovementFeeling," +
                "symptomType,symptomSeverity,exerciseType,exerciseMinutes,exerciseIntensity,dietMeal,dietContent,appointmentTime," +
                "appointmentLocation,appointmentDoctor,appointmentItems,appointmentResult,note,createdAtEpochMillis\n" +
                "record-1,2026-06-21,Appointment,,,,,,,,,,,,09:30,\"Clinic, Room 2\",Dr Chen,Blood test,Normal," +
                "\"Bring \"\"old\"\" report\nand water\",42\n",
            csv,
        )
    }

    @Test
    fun `protects exported cells that could be interpreted as spreadsheet formulas`() {
        val csv = exporter.export(
            listOf(
                CalendarRecord(
                    id = "record-1",
                    date = LocalDate.of(2026, 6, 21),
                    type = CalendarRecordType.Note,
                    note = "=IMPORTXML(\"https://example.com\",\"//title\")",
                    weightKg = null,
                    fetalMovementCount = null,
                    fetalMovementPeriod = null,
                    fetalMovementFeeling = null,
                    symptomType = null,
                    symptomSeverity = null,
                    exerciseType = null,
                    exerciseMinutes = null,
                    exerciseIntensity = null,
                    dietMeal = null,
                    dietContent = null,
                    appointmentTime = null,
                    appointmentLocation = null,
                    appointmentDoctor = null,
                    appointmentItems = "+sensitive lab result",
                    appointmentResult = null,
                    createdAtEpochMillis = 42L,
                ),
            ),
        )

        assertEquals(
            "\uFEFFid,date,type,weightKg,fetalMovementCount,fetalMovementPeriod,fetalMovementFeeling," +
                "symptomType,symptomSeverity,exerciseType,exerciseMinutes,exerciseIntensity,dietMeal,dietContent,appointmentTime," +
                "appointmentLocation,appointmentDoctor,appointmentItems,appointmentResult,note,createdAtEpochMillis\n" +
                "record-1,2026-06-21,Note,,,,,,,,,,,,,,,'+sensitive lab result,," +
                "\"'=IMPORTXML(\"\"https://example.com\"\",\"\"//title\"\")\",42\n",
            csv,
        )
    }

    @Test
    fun `exports records in stable chronological order`() {
        val csv = exporter.export(
            listOf(
                record(id = "later-date", date = LocalDate.of(2026, 6, 22), createdAtEpochMillis = 1L),
                record(id = "same-date-later", date = LocalDate.of(2026, 6, 21), createdAtEpochMillis = 2L),
                record(id = "same-date-earlier", date = LocalDate.of(2026, 6, 21), createdAtEpochMillis = 1L),
                record(id = "same-date-same-time-a", date = LocalDate.of(2026, 6, 21), createdAtEpochMillis = 1L),
            ),
        )

        val exportedIds = csv
            .lineSequence()
            .drop(1)
            .filter(String::isNotBlank)
            .map { line -> line.substringBefore(",") }
            .toList()

        assertEquals(
            listOf("same-date-earlier", "same-date-same-time-a", "same-date-later", "later-date"),
            exportedIds,
        )
    }

    private fun record(
        id: String,
        date: LocalDate,
        createdAtEpochMillis: Long,
    ): CalendarRecord = CalendarRecord(
        id = id,
        date = date,
        type = CalendarRecordType.Note,
        note = "note",
        weightKg = null,
        fetalMovementCount = null,
        fetalMovementPeriod = null,
        fetalMovementFeeling = null,
        symptomType = null,
        symptomSeverity = null,
        exerciseType = null,
        exerciseMinutes = null,
        exerciseIntensity = null,
        dietMeal = null,
        dietContent = null,
        appointmentTime = null,
        appointmentLocation = null,
        appointmentDoctor = null,
        appointmentItems = null,
        appointmentResult = null,
        createdAtEpochMillis = createdAtEpochMillis,
    )
}
