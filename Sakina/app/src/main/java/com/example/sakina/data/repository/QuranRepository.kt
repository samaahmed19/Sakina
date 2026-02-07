package com.example.sakina.data.repository

import android.content.Context
import com.example.sakina.data.local.database.dao.QuranDao
import com.example.sakina.data.local.database.dao.TafsirDao
import com.example.sakina.data.local.database.entity.AyahEntity
import com.example.sakina.data.local.database.entity.SurahEntity
import com.example.sakina.data.local.database.entity.TafsirEntity
import com.example.sakina.domain.model.AyahTafsir
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map

@Singleton
class QuranRepository @Inject constructor(
    private val quranDao: QuranDao,
    private val tafsirDao: TafsirDao,
    @ApplicationContext private val context: Context
) {

    suspend fun getAllSurahs(): List<SurahEntity> = quranDao.getAllSurahs()

    suspend fun getAyahsBySurah(surahId: Int): List<AyahEntity> = quranDao.getAyahsBySurah(surahId)

    suspend fun getTafsirBySurah(surahId: Int): List<TafsirEntity> = withContext(Dispatchers.IO) {
        checkAndLoadTafsirIntoDb()
        tafsirDao.getTafsirBySurah(surahId)
    }

    private suspend fun checkAndLoadTafsirIntoDb() = withContext(Dispatchers.IO) {
        if (tafsirDao.getTafsirCount() == 0) {
            try {
                val jsonString = context.assets.open("tafsir.json").bufferedReader().use { it.readText() }
                val listType = object : TypeToken<List<AyahTafsir>>() {}.type
                val jsonList: List<AyahTafsir> = Gson().fromJson(jsonString, listType)

                val entities = jsonList.map {
                    TafsirEntity(
                        surahId = it.number.toInt(),
                        ayahNumber = it.aya.toInt(),
                        text = it.text
                    )
                }
                tafsirDao.insertTafsirList(entities)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}