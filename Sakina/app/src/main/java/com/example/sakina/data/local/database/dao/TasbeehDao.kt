package com.example.sakina.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sakina.data.local.database.entity.TasbeehEntity

@Dao
interface TasbeehDao {

    @Query("SELECT * FROM tasbeeh ORDER BY priority ASC")
    suspend fun getAll(): List<TasbeehEntity>

    @Query("UPDATE tasbeeh SET currentCount = currentCount + 1 WHERE id = :id")
    suspend fun increment(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<TasbeehEntity>)
}