package com.example.sakina.data.source

import android.content.Context
import com.example.sakina.data.local.database.dao.AzkarDao
import com.example.sakina.data.local.repository.DuaRepository
import com.example.sakina.data.source.mapper.JsonMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataInitializer @Inject constructor(
    private val context: Context,
    private val azkarDao: AzkarDao,
    private val duaRepository: DuaRepository
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

    suspend fun initDuasIfNeeded() = withContext(Dispatchers.IO) {
        if (duaRepository.getCount() > 0) return@withContext

        try {
            val json = context.assets
                .open("dua.json")
                .bufferedReader()
                .use { it.readText() }

            val duasList = JsonMapper.mapDuas(json)

            duaRepository.insertAllDuas(duasList)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

