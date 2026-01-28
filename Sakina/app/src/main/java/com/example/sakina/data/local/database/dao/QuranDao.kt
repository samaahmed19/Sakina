package com.example.sakina.data.local.database.dao

import androidx.room.*
import com.example.sakina.data.local.database.entity.AyahEntity
import com.example.sakina.data.local.database.entity.SurahEntity

@Dao
interface QuranDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    @Query("SELECT * FROM surahs")
    suspend fun getAllSurahs(): List<SurahEntity>

    @Query("SELECT * FROM ayahs WHERE surahId = :surahId")
    suspend fun getAyahsBySurah(surId: Int): List<AyahEntity>
}