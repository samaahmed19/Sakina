package com.example.sakina.data.repository

import com.example.sakina.data.local.database.dao.QuranDao
import com.example.sakina.domain.repository.QuranRepository
import javax.inject.Inject

class QuranRepositoryImpl @Inject constructor(
    private val quranDao: QuranDao
) : QuranRepository {
    override suspend fun getSurahs() = quranDao.getAllSurahs()
    override suspend fun getAyahsBySurah(surahId: Int) = quranDao.getAyahsBySurah(surahId)
}