package com.yunqi.app.data.local.record

import android.content.Context
import com.yunqi.app.data.local.db.YunqiDatabase
import com.yunqi.app.domain.calendar.CalendarRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class CalendarRecordRepository(context: Context) {
    private val dao = YunqiDatabase.getInstance(context).calendarRecordDao()

    /**
     * Emits all locally stored records for the selected date.
     */
    fun recordsForDate(date: LocalDate): Flow<List<CalendarRecord>> =
        dao.recordsForDate(date.toString()).map { records ->
            records.map(CalendarRecordEntity::toDomain)
        }

    /**
     * Emits all local records for aggregate views such as trends.
     */
    fun allRecords(): Flow<List<CalendarRecord>> =
        dao.allRecords().map { records ->
            records.map(CalendarRecordEntity::toDomain)
        }

    /**
     * Emits records within an inclusive local date range.
     */
    fun recordsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<CalendarRecord>> =
        dao.recordsBetween(startDate.toString(), endDate.toString()).map { records ->
            records.map(CalendarRecordEntity::toDomain)
        }

    /**
     * Returns future appointment records so reminder settings can rebuild scheduled work.
     */
    suspend fun futureAppointmentRecords(fromDate: LocalDate): List<CalendarRecord> =
        dao.futureAppointmentRecords(fromDate.toString()).map(CalendarRecordEntity::toDomain)

    /**
     * Saves a calendar record to the private local Room database.
     */
    suspend fun save(record: CalendarRecord) {
        dao.upsert(record.toEntity())
    }

    suspend fun delete(id: String) {
        dao.deleteById(id)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
