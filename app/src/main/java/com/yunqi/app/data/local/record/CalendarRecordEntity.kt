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
    val exerciseMinutes: Int?,
    val appointmentTime: String?,
    val appointmentLocation: String?,
    val createdAtEpochMillis: Long,
)

fun CalendarRecordEntity.toDomain(): CalendarRecord = CalendarRecord(
    id = id,
    date = LocalDate.parse(date),
    type = CalendarRecordType.valueOf(type),
    note = note,
    weightKg = weightKg,
    fetalMovementCount = fetalMovementCount,
    exerciseMinutes = exerciseMinutes,
    appointmentTime = appointmentTime,
    appointmentLocation = appointmentLocation,
    createdAtEpochMillis = createdAtEpochMillis,
)

fun CalendarRecord.toEntity(): CalendarRecordEntity = CalendarRecordEntity(
    id = id,
    date = date.toString(),
    type = type.name,
    note = note,
    weightKg = weightKg,
    fetalMovementCount = fetalMovementCount,
    exerciseMinutes = exerciseMinutes,
    appointmentTime = appointmentTime,
    appointmentLocation = appointmentLocation,
    createdAtEpochMillis = createdAtEpochMillis,
)
