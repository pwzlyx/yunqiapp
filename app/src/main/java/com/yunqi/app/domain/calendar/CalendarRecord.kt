package com.yunqi.app.domain.calendar

import java.time.LocalDate

enum class CalendarRecordType {
    Appointment,
    Weight,
    FetalMovement,
    Symptom,
    Exercise,
    Diet,
    Note,
}

data class CalendarRecord(
    val id: String,
    val date: LocalDate,
    val type: CalendarRecordType,
    val note: String,
    val weightKg: Double?,
    val fetalMovementCount: Int?,
    val fetalMovementPeriod: String?,
    val fetalMovementFeeling: String?,
    val symptomType: String? = null,
    val symptomSeverity: String? = null,
    val exerciseType: String? = null,
    val exerciseMinutes: Int?,
    val exerciseIntensity: String? = null,
    val dietMeal: String? = null,
    val dietContent: String? = null,
    val appointmentTime: String?,
    val appointmentLocation: String?,
    val appointmentDoctor: String?,
    val appointmentItems: String?,
    val appointmentResult: String?,
    val createdAtEpochMillis: Long,
)
