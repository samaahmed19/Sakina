package com.example.sakina.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sakina.data.local.database.entity.StreakEntity

@Dao
interface StreakDao {

    @Query("SELECT * FROM streak WHERE id = 0")
    suspend fun getStreak(): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStreak(streak: StreakEntity)

    @Query("DELETE FROM streak")
    suspend fun clearStreak()
}

