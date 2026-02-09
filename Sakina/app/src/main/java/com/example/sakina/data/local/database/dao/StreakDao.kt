package com.example.sakina.data.local.database.dao

import androidx.room.*
import com.example.sakina.data.local.database.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streak WHERE id = 1")
    fun getStreak(): Flow<StreakEntity?>

    @Query("SELECT * FROM streak WHERE id = 1")
    suspend fun getStreakOnce(): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStreak(streak: StreakEntity)
}