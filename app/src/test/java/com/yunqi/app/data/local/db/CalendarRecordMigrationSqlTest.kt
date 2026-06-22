package com.yunqi.app.data.local.db

import org.junit.Assert.assertEquals
import org.junit.Test

class CalendarRecordMigrationSqlTest {
    @Test
    fun `migration sql keeps every calendar record column added after version one`() {
        val migrationStatements = CalendarRecordMigrationSql.V1_TO_V2 +
            CalendarRecordMigrationSql.V2_TO_V3 +
            CalendarRecordMigrationSql.V3_TO_V4 +
            CalendarRecordMigrationSql.V4_TO_V5

        assertEquals(
            listOf(
                "exerciseMinutes",
                "appointmentDoctor",
                "appointmentItems",
                "appointmentResult",
                "fetalMovementPeriod",
                "fetalMovementFeeling",
                "symptomType",
                "symptomSeverity",
                "exerciseType",
                "exerciseIntensity",
                "dietMeal",
                "dietContent",
            ),
            migrationStatements.map { statement ->
                statement.substringAfter("ADD COLUMN ").substringBefore(" ")
            },
        )
    }

    @Test
    fun `migration sql uses nullable column additions to preserve existing local records`() {
        val migrationStatements = CalendarRecordMigrationSql.V1_TO_V2 +
            CalendarRecordMigrationSql.V2_TO_V3 +
            CalendarRecordMigrationSql.V3_TO_V4 +
            CalendarRecordMigrationSql.V4_TO_V5

        assertEquals(
            emptyList<String>(),
            migrationStatements.filter { statement ->
                statement.contains(" NOT NULL", ignoreCase = true) ||
                    statement.contains(" DEFAULT ", ignoreCase = true)
            },
        )
    }
}
