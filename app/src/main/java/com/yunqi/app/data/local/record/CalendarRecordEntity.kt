package com.yunqi.app.data.local.record

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import java.time.LocalDate

@Entity(tableName = "calendar_records")
data class CalendarRecordEntity(
    @PrimaryKey val id: String,
    val date: String,
    val type: String,
    val note: String,
    val weightKg: Double?,
    val fetalMovementCount: Int?,
    val fetalMovementPeriod: String?,
    val fetalMovementFeeling: String?,
    val symptomType: String?,
    val symptomSeverity: String?,
    val exerciseType: String?,
    val exerciseMinutes: Int?,
    val exerciseIntensity: String?,
    val dietMeal: String?,
    val dietContent: String?,
    val appointmentTime: String?,
    val appointmentLocation: String?,
    val appointmentDoctor: String?,
    val appointmentItems: String?,
    val appointmentResult: String?,
    val createdAtEpochMillis: Long,
)

fun CalendarRecordEntity.toDomain(): CalendarRecord = CalendarRecord(
    id = id,
    date = LocalDate.parse(date),
    type = CalendarRecordType.valueOf(type),
    note = note,
    weightKg = weightKg,
    fetalMovementCount = fetalMovementCount,
    fetalMovementPeriod = fetalMovementPeriod,
    fetalMovementFeeling = fetalMovementFeeling,
    symptomType = symptomType,
    symptomSeverity = symptomSeverity,
    exerciseType = exerciseType,
    exerciseMinutes = exerciseMinutes,
    exerciseIntensity = exerciseIntensity,
    dietMeal = dietMeal,
    dietContent = dietContent,
    appointmentTime = appointmentTime,
    appointmentLocation = appointmentLocation,
    appointmentDoctor = appointmentDoctor,
    appointmentItems = appointmentItems,
    appointmentResult = appointmentResult,
    createdAtEpochMillis = createdAtEpochMillis,
)

fun CalendarRecord.toEntity(): CalendarRecordEntity = CalendarRecordEntity(
    id = id,
    date = date.toString(),
    type = type.name,
    note = note,
    weightKg = weightKg,
    fetalMovementCount = fetalMovementCount,
    fetalMovementPeriod = fetalMovementPeriod,
    fetalMovementFeeling = fetalMovementFeeling,
    symptomType = symptomType,
    symptomSeverity = symptomSeverity,
    exerciseType = exerciseType,
    exerciseMinutes = exerciseMinutes,
    exerciseIntensity = exerciseIntensity,
    dietMeal = dietMeal,
    dietContent = dietContent,
    appointmentTime = appointmentTime,
    appointmentLocation = appointmentLocation,
    appointmentDoctor = appointmentDoctor,
    appointmentItems = appointmentItems,
    appointmentResult = appointmentResult,
    createdAtEpochMillis = createdAtEpochMillis,
)
