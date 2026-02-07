package com.example.sakina.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sakina.data.local.database.entity.TafsirEntity

@Dao
interface TafsirDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTafsirList(list: List<TafsirEntity>)

    @Query("SELECT * FROM tafsir WHERE surahId = :surahId")
    suspend fun getTafsirBySurah(surahId: Int): List<TafsirEntity>

    @Query("SELECT COUNT(*) FROM tafsir")
    suspend fun getTafsirCount(): Int
}