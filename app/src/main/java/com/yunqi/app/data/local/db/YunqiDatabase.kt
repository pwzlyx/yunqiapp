package com.yunqi.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yunqi.app.data.local.record.CalendarRecordDao
import com.yunqi.app.data.local.record.CalendarRecordEntity

@Database(
    entities = [CalendarRecordEntity::class],
    version = 5,
    exportSchema = false,
)
abstract class YunqiDatabase : RoomDatabase() {
    abstract fun calendarRecordDao(): CalendarRecordDao

    companion object {
        @Volatile
        private var instance: YunqiDatabase? = null

        /**
         * Returns the singleton local database used for private on-device records.
         */
        fun getInstance(context: Context): YunqiDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    YunqiDatabase::class.java,
                    "yunqi.db",
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                    .also { instance = it }
            }

        private val MIGRATION_1_2 = columnAdditionMigration(
            startVersion = 1,
            endVersion = 2,
            statements = CalendarRecordMigrationSql.V1_TO_V2,
        )

        private val MIGRATION_2_3 = columnAdditionMigration(
            startVersion = 2,
            endVersion = 3,
            statements = CalendarRecordMigrationSql.V2_TO_V3,
        )

        private val MIGRATION_3_4 = columnAdditionMigration(
            startVersion = 3,
            endVersion = 4,
            statements = CalendarRecordMigrationSql.V3_TO_V4,
        )

        private val MIGRATION_4_5 = columnAdditionMigration(
            startVersion = 4,
            endVersion = 5,
            statements = CalendarRecordMigrationSql.V4_TO_V5,
        )

        private fun columnAdditionMigration(
            startVersion: Int,
            endVersion: Int,
            statements: List<String>,
        ) = object : Migration(startVersion, endVersion) {
            override fun migrate(db: SupportSQLiteDatabase) {
                statements.forEach(db::execSQL)
            }
        }
    }
}

internal object CalendarRecordMigrationSql {
    val V1_TO_V2 = listOf(
        "ALTER TABLE calendar_records ADD COLUMN exerciseMinutes INTEGER",
    )
    val V2_TO_V3 = listOf(
        "ALTER TABLE calendar_records ADD COLUMN appointmentDoctor TEXT",
        "ALTER TABLE calendar_records ADD COLUMN appointmentItems TEXT",
        "ALTER TABLE calendar_records ADD COLUMN appointmentResult TEXT",
    )
    val V3_TO_V4 = listOf(
        "ALTER TABLE calendar_records ADD COLUMN fetalMovementPeriod TEXT",
        "ALTER TABLE calendar_records ADD COLUMN fetalMovementFeeling TEXT",
    )
    val V4_TO_V5 = listOf(
        "ALTER TABLE calendar_records ADD COLUMN symptomType TEXT",
        "ALTER TABLE calendar_records ADD COLUMN symptomSeverity TEXT",
        "ALTER TABLE calendar_records ADD COLUMN exerciseType TEXT",
        "ALTER TABLE calendar_records ADD COLUMN exerciseIntensity TEXT",
        "ALTER TABLE calendar_records ADD COLUMN dietMeal TEXT",
        "ALTER TABLE calendar_records ADD COLUMN dietContent TEXT",
    )
}
