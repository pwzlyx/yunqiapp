package com.yunqi.app.feature.calendar

import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import com.yunqi.app.domain.calendar.toOptionalStoredCalendarRecordText
import com.yunqi.app.domain.calendar.toStoredCalendarRecordText
import com.yunqi.app.core.time.isStrictHourMinute
import java.time.LocalDate
import java.util.UUID

private const val MAX_WEIGHT_KG = 300.0
private const val MAX_FETAL_MOVEMENT_COUNT = 1_000
private const val MAX_EXERCISE_MINUTES_PER_DAY = 24 * 60

class CalendarRecordFormParser(
    private val idProvider: () -> String = { UUID.randomUUID().toString() },
    private val nowProvider: () -> Long = System::currentTimeMillis,
) {
    /**
     * Validates raw form input and returns a domain record ready to persist.
     */
    fun parse(input: CalendarRecordInput): CalendarRecordParseResult {
        val date = input.date.parseDate()
            ?: return CalendarRecordParseResult.InvalidDate
        val weightKg = input.weightKg.parseOptionalDouble()
        val fetalMovementCount = input.fetalMovementCount.parseOptionalInt()
        val exerciseMinutes = input.exerciseMinutes.parseOptionalInt()

        if (input.type == CalendarRecordType.Weight && !weightKg.isValidWeightKg()) {
            return CalendarRecordParseResult.InvalidWeight
        }
        if (
            input.type == CalendarRecordType.FetalMovement &&
            !input.hasValidFetalMovementInput(fetalMovementCount)
        ) {
            return CalendarRecordParseResult.InvalidFetalMovement
        }
        if (input.type == CalendarRecordType.Exercise && !exerciseMinutes.isValidExerciseMinutes()) {
            return CalendarRecordParseResult.InvalidExerciseMinutes
        }
        if (
            input.type == CalendarRecordType.Appointment &&
            input.appointmentTime.isNotBlank() &&
            !input.appointmentTime.isStrictHourMinute()
        ) {
            return CalendarRecordParseResult.InvalidAppointmentTime
        }
        if (!input.hasRequiredContent()) {
            return CalendarRecordParseResult.InvalidRecordContent
        }

        return CalendarRecordParseResult.Success(
            CalendarRecord(
                id = input.id ?: idProvider(),
                date = date,
                type = input.type,
                note = input.note.toStoredCalendarRecordText(),
                weightKg = if (input.type == CalendarRecordType.Weight) weightKg else null,
                fetalMovementCount = if (input.type == CalendarRecordType.FetalMovement) fetalMovementCount else null,
                fetalMovementPeriod = input.fetalMovementPeriod.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.FetalMovement },
                fetalMovementFeeling = input.fetalMovementFeeling.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.FetalMovement },
                symptomType = input.symptomType.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.Symptom },
                symptomSeverity = input.symptomSeverity.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.Symptom },
                exerciseType = input.exerciseType.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.Exercise },
                exerciseMinutes = if (input.type == CalendarRecordType.Exercise) exerciseMinutes else null,
                exerciseIntensity = input.exerciseIntensity.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.Exercise },
                dietMeal = input.dietMeal.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.Diet },
                dietContent = input.dietContent.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.Diet },
                appointmentTime = input.appointmentTime.trim().takeIf {
                    input.type == CalendarRecordType.Appointment && it.isNotBlank()
                },
                appointmentLocation = input.appointmentLocation.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.Appointment },
                appointmentDoctor = input.appointmentDoctor.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.Appointment },
                appointmentItems = input.appointmentItems.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.Appointment },
                appointmentResult = input.appointmentResult.toOptionalStoredCalendarRecordText()
                    .takeIf { input.type == CalendarRecordType.Appointment },
                createdAtEpochMillis = input.createdAtEpochMillis ?: nowProvider(),
            ),
        )
    }

    private fun String.parseDate(): LocalDate? = runCatching {
        LocalDate.parse(trim())
    }.getOrNull()

    private fun String.parseOptionalDouble(): Double? =
        trim().takeIf(String::isNotEmpty)?.toDoubleOrNull()

    private fun String.parseOptionalInt(): Int? =
        trim().takeIf(String::isNotEmpty)?.toIntOrNull()

    private fun Double?.isValidWeightKg(): Boolean =
        this != null && this > 0.0 && this <= MAX_WEIGHT_KG

    private fun Int?.isValidFetalMovementCount(): Boolean =
        this != null && this in 0..MAX_FETAL_MOVEMENT_COUNT

    private fun CalendarRecordInput.hasValidFetalMovementInput(count: Int?): Boolean {
        if (fetalMovementCount.isNotBlank()) {
            return count.isValidFetalMovementCount()
        }
        return fetalMovementFeeling.isNotBlank()
    }

    private fun CalendarRecordInput.hasRequiredContent(): Boolean = when (type) {
        CalendarRecordType.Appointment -> anyNonBlank(
            appointmentTime,
            appointmentLocation,
            appointmentDoctor,
            appointmentItems,
            appointmentResult,
            note,
        )
        CalendarRecordType.Symptom -> anyNonBlank(symptomType, symptomSeverity, note)
        CalendarRecordType.Diet -> anyNonBlank(dietMeal, dietContent, note)
        CalendarRecordType.Note -> note.isNotBlank()
        else -> true
    }

    private fun anyNonBlank(vararg values: String): Boolean =
        values.any(String::isNotBlank)

    private fun Int?.isValidExerciseMinutes(): Boolean =
        this != null && this in 1..MAX_EXERCISE_MINUTES_PER_DAY
}

data class CalendarRecordInput(
    val id: String? = null,
    val date: String,
    val type: CalendarRecordType,
    val note: String,
    val weightKg: String,
    val fetalMovementCount: String,
    val fetalMovementPeriod: String = "",
    val fetalMovementFeeling: String = "",
    val symptomType: String = "",
    val symptomSeverity: String = "",
    val exerciseType: String = "",
    val exerciseMinutes: String = "",
    val exerciseIntensity: String = "",
    val dietMeal: String = "",
    val dietContent: String = "",
    val appointmentTime: String,
    val appointmentLocation: String,
    val appointmentDoctor: String = "",
    val appointmentItems: String = "",
    val appointmentResult: String = "",
    val createdAtEpochMillis: Long? = null,
)

sealed interface CalendarRecordParseResult {
    data class Success(val record: CalendarRecord) : CalendarRecordParseResult
    data object InvalidDate : CalendarRecordParseResult
    data object InvalidWeight : CalendarRecordParseResult
    data object InvalidFetalMovement : CalendarRecordParseResult
    data object InvalidExerciseMinutes : CalendarRecordParseResult
    data object InvalidAppointmentTime : CalendarRecordParseResult
    data object InvalidRecordContent : CalendarRecordParseResult
}
