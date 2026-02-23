package com.sama.sakina.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sama.sakina.data.local.database.entity.PrayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerDao {

    @Query("SELECT * FROM prayer_completion WHERE date = :date")
    suspend fun getByDate(date: String): List<PrayerEntity>

    @Query("""
        SELECT date, 
        (SUM(CASE WHEN type = 'NAFILA' AND isCompleted = 1 THEN 1 ELSE 0 END) * 10) + 
         SUM(CASE WHEN type = 'FARD' AND isCompleted = 1 THEN 1 ELSE 0 END) as value
        FROM prayer_completion 
        WHERE date LIKE :monthQuery || '%'
        GROUP BY date
    """)

    fun getMonthlySummary(
        monthQuery: String
    ): Flow<Map<@MapColumn(columnName = "date") String, @MapColumn(columnName = "value") Int>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PrayerEntity)

    @Query("DELETE FROM prayer_completion WHERE date = :date")
    suspend fun deleteByDate(date: String)
}
