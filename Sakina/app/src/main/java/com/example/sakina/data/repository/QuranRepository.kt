package com.example.sakina.data.repository

import com.example.sakina.data.local.database.dao.QuranDao
import com.example.sakina.data.local.database.entity.AyahEntity
import com.example.sakina.data.local.database.entity.SurahEntity
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class QuranRepository @Inject constructor(
    private val quranDao: QuranDao
) {


    suspend fun getAllSurahs(): List<SurahEntity> {
        return quranDao.getAllSurahs()
    }


    suspend fun getAyahsBySurah(surahId: Int): List<AyahEntity> {
        return quranDao.getAyahsBySurah(surahId)
    }



}
