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
    version = 4,
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { instance = it }
            }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE calendar_records ADD COLUMN exerciseMinutes INTEGER")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE calendar_records ADD COLUMN appointmentDoctor TEXT")
                db.execSQL("ALTER TABLE calendar_records ADD COLUMN appointmentItems TEXT")
                db.execSQL("ALTER TABLE calendar_records ADD COLUMN appointmentResult TEXT")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE calendar_records ADD COLUMN fetalMovementPeriod TEXT")
                db.execSQL("ALTER TABLE calendar_records ADD COLUMN fetalMovementFeeling TEXT")
            }
        }
    }
}
