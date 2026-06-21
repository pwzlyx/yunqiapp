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
                    "exerciseMinutes",
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
                        record.exerciseMinutes?.toString().orEmpty(),
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
        return rows.joinToString(separator = "\n", postfix = "\n") { row ->
            row.joinToString(separator = ",", transform = ::escape)
        }
    }

    private fun escape(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return if (escaped.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"$escaped\""
        } else {
            escaped
        }
    }
}
