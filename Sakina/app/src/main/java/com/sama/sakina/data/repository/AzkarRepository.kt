package com.sama.sakina.data.repository
import android.content.Context
import com.sama.sakina.data.local.database.dao.AzkarDao
import com.sama.sakina.data.local.database.entity.CategoryEntity
import com.sama.sakina.data.local.database.entity.ZikrEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AzkarRepository @Inject constructor(
    private val azkarDao: AzkarDao,@ApplicationContext private val context: Context
) {

    suspend fun getAllCategories(): List<CategoryEntity> {
        return azkarDao.getAllCategories()
    }

    suspend fun getCategoryById(categoryId: String): CategoryEntity {
        return azkarDao.getCategoryById(categoryId)
    }

    suspend fun insertCategories(categories: List<CategoryEntity>) {
        azkarDao.insertCategories(categories)
    }

    suspend fun getAzkarByCategory(categoryId: String): List<ZikrEntity> {
        return azkarDao.getAzkarByCategory(categoryId)
    }

    suspend fun insertAzkar(azkar: List<ZikrEntity>) {
        azkarDao.insertAzkar(azkar)
    }fun loadJsonFromAssets(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    suspend fun resetOldAzkar(todayStart: Long) {
        azkarDao.resetOldAzkar(todayStart)
    }

    suspend fun updateZikrWithTimestamp(zikrId: Int, count: Int, timestamp: Long) {
        azkarDao.updateZikrWithTimestamp(zikrId, count, timestamp)
    }
}
