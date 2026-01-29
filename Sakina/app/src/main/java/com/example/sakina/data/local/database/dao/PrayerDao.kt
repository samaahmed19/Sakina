package com.example.sakina.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sakina.data.local.database.entity.PrayerEntity

@Dao
interface PrayerDao {

    @Query("SELECT * FROM prayer_completion WHERE date = :date")
    suspend fun getByDate(date: String): List<PrayerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PrayerEntity)

    @Query("DELETE FROM prayer_completion WHERE date = :date")
    suspend fun deleteByDate(date: String)
}
