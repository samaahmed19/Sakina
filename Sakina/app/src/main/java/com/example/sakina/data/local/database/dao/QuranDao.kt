package com.example.sakina.data.local.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sakina.data.local.database.entity.AyahEntity
import com.example.sakina.data.local.database.entity.SurahEntity
@Dao
interface QuranDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahs(ayahs: List<AyahEntity>)

    @Query("SELECT * FROM surahs ORDER BY id ASC")
    suspend fun getAllSurahs(): List<SurahEntity>

    @Query("SELECT * FROM ayahs WHERE surahId = :surahId ORDER BY number ASC")
    suspend fun getAyahsBySurah(surahId: Int): List<AyahEntity>
}