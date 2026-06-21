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
            "id,date,type,weightKg,fetalMovementCount,exerciseMinutes,appointmentTime," +
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
                    exerciseMinutes = null,
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
            "id,date,type,weightKg,fetalMovementCount,exerciseMinutes,appointmentTime," +
                "appointmentLocation,appointmentDoctor,appointmentItems,appointmentResult,note,createdAtEpochMillis\n" +
                "record-1,2026-06-21,Appointment,,,,09:30,\"Clinic, Room 2\",Dr Chen,Blood test,Normal," +
                "\"Bring \"\"old\"\" report\nand water\",42\n",
            csv,
        )
    }
}
