package com.yunqi.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yunqi.app.data.local.record.CalendarRecordDao
import com.yunqi.app.data.local.record.CalendarRecordEntity

@Database(
    entities = [CalendarRecordEntity::class],
    version = 1,
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
                ).build().also { instance = it }
            }
    }
}

