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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: CalendarRecordEntity)

    @Query("DELETE FROM calendar_records WHERE id = :id")
    suspend fun deleteById(id: String)
}

