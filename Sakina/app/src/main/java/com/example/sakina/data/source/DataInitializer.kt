package com.example.sakina.data.source

import android.content.Context
import com.example.sakina.data.local.database.dao.AzkarDao
import com.example.sakina.data.local.repository.DuaRepository
import com.example.sakina.data.local.database.dao.QuranDao
import com.example.sakina.data.local.database.entity.AyahEntity
import com.example.sakina.data.local.database.entity.SurahEntity
import com.example.sakina.data.source.mapper.JsonMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val azkarDao: AzkarDao ,
    private val duaRepository: DuaRepository,
    private val quranDao: QuranDao
) {

    suspend fun initAzkarIfNeeded() = withContext(Dispatchers.IO) {
        if (azkarDao.getAllCategories().isNotEmpty()) return@withContext

        val json = context.assets
            .open("azkar.json")
            .bufferedReader()
            .use { it.readText() }

        val (categories, azkar) = JsonMapper.mapCategories(json)

        azkarDao.insertCategories(categories)
        azkarDao.insertAzkar(azkar)
    }
    suspend fun initQuranIfNeeded() = withContext(Dispatchers.IO) {
        if (quranDao.getAllSurahs().isNotEmpty()) return@withContext

        val jsonString = context.assets
            .open("quran.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonArray = JSONArray(jsonString)
        val surahs = mutableListOf<SurahEntity>()
        val ayahs = mutableListOf<AyahEntity>()

        for (i in 0 until jsonArray.length()) {
            val surahObject = jsonArray.getJSONObject(i)
            val surahId = surahObject.getInt("id")

            surahs.add(
                SurahEntity(
                    id = surahId,
                    nameAr = surahObject.getString("name"),
                    nameEn = surahObject.getString("englishName"),
                    ayahCount = surahObject.getJSONArray("verses").length()
                )
            )

            val versesArray = surahObject.getJSONArray("verses")
            for (j in 0 until versesArray.length()) {
                val verseObject = versesArray.getJSONObject(j)
                ayahs.add(
                    AyahEntity(
                        surahId = surahId,
                        text = verseObject.getString("text"),
                        number = verseObject.getInt("numberInSurah")
                    )
                )
            }
        }

        quranDao.insertSurahs(surahs)
        quranDao.insertAyahs(ayahs)
    }
}



