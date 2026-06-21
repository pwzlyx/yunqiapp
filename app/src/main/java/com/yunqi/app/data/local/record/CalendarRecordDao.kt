package com.yunqi.app.data.local.record

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarRecordDao {
    @Query(
        """
        SELECT * FROM calendar_records
        WHERE date = :date
        ORDER BY createdAtEpochMillis DESC
        """,
    )
    fun recordsForDate(date: String): Flow<List<CalendarRecordEntity>>

    @Query("SELECT * FROM calendar_records ORDER BY date DESC, createdAtEpochMillis DESC")
    fun allRecords(): Flow<List<CalendarRecordEntity>>

    @Query("SELECT * FROM calendar_records ORDER BY date ASC, createdAtEpochMillis ASC")
    suspend fun allRecordsSnapshot(): List<CalendarRecordEntity>

    @Query(
        """
        SELECT * FROM calendar_records
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date ASC, createdAtEpochMillis DESC
        """,
    )
    fun recordsBetween(startDate: String, endDate: String): Flow<List<CalendarRecordEntity>>

    @Query(
        """
        SELECT * FROM calendar_records
        WHERE type = 'Appointment' AND date >= :fromDate
        ORDER BY date ASC, appointmentTime ASC
        """,
    )
    suspend fun futureAppointmentRecords(fromDate: String): List<CalendarRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: CalendarRecordEntity)

    @Query("DELETE FROM calendar_records WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM calendar_records")
    suspend fun deleteAll()
}
