package com.yunqi.app.data.export

import com.yunqi.app.domain.calendar.CalendarRecord

class CalendarRecordCsvExporter {
    /**
     * Converts local calendar records into a UTF-8 CSV document suitable for user export.
     */
    fun export(records: List<CalendarRecord>): String {
        val rows = buildList {
            add(
                listOf(
                    "id",
                    "date",
                    "type",
                    "weightKg",
                    "fetalMovementCount",
                    "fetalMovementPeriod",
                    "fetalMovementFeeling",
                    "symptomType",
                    "symptomSeverity",
                    "exerciseType",
                    "exerciseMinutes",
                    "exerciseIntensity",
                    "dietMeal",
                    "dietContent",
                    "appointmentTime",
                    "appointmentLocation",
                    "appointmentDoctor",
                    "appointmentItems",
                    "appointmentResult",
                    "note",
                    "createdAtEpochMillis",
                ),
            )
            records.forEach { record ->
                add(
                    listOf(
                        record.id,
                        record.date.toString(),
                        record.type.name,
                        record.weightKg?.toString().orEmpty(),
                        record.fetalMovementCount?.toString().orEmpty(),
                        record.fetalMovementPeriod.orEmpty(),
                        record.fetalMovementFeeling.orEmpty(),
                        record.symptomType.orEmpty(),
                        record.symptomSeverity.orEmpty(),
                        record.exerciseType.orEmpty(),
                        record.exerciseMinutes?.toString().orEmpty(),
                        record.exerciseIntensity.orEmpty(),
                        record.dietMeal.orEmpty(),
                        record.dietContent.orEmpty(),
                        record.appointmentTime.orEmpty(),
                        record.appointmentLocation.orEmpty(),
                        record.appointmentDoctor.orEmpty(),
                        record.appointmentItems.orEmpty(),
                        record.appointmentResult.orEmpty(),
                        record.note,
                        record.createdAtEpochMillis.toString(),
                    ),
                )
            }
        }
        return UTF_8_BOM + rows.joinToString(separator = "\n", postfix = "\n") { row ->
            row.joinToString(separator = ",", transform = ::escape)
        }
    }

    private fun escape(value: String): String {
        val escaped = value.withSpreadsheetFormulaProtection().replace("\"", "\"\"")
        return if (escaped.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"$escaped\""
        } else {
            escaped
        }
    }

    private fun String.withSpreadsheetFormulaProtection(): String {
        val firstMeaningfulCharacter = firstOrNull { !it.isWhitespace() } ?: return this
        return if (firstMeaningfulCharacter in formulaPrefixCharacters) {
            "'$this"
        } else {
            this
        }
    }

    private companion object {
        // Keeps Chinese CSV text readable in spreadsheet apps that infer legacy encodings.
        const val UTF_8_BOM = "\uFEFF"
        val formulaPrefixCharacters = setOf('=', '+', '-', '@')
    }
}
