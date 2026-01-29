package com.example.sakina.data.source

import android.content.Context
import com.example.sakina.data.local.database.dao.AzkarDao
import com.example.sakina.data.source.mapper.JsonMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val azkarDao: AzkarDao
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
}
