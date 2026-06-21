package com.yunqi.app.domain.calendar

import java.time.LocalDate

enum class CalendarRecordType {
    Appointment,
    Weight,
    FetalMovement,
    Note,
}

data class CalendarRecord(
    val id: String,
    val date: LocalDate,
    val type: CalendarRecordType,
    val note: String,
    val weightKg: Double?,
    val fetalMovementCount: Int?,
    val appointmentTime: String?,
    val appointmentLocation: String?,
    val createdAtEpochMillis: Long,
)

