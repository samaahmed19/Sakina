package com.example.sakina.data.repository
import com.example.sakina.data.local.database.dao.AzkarDao
import com.example.sakina.data.local.database.entity.CategoryEntity
import com.example.sakina.data.local.database.entity.ZikrEntity
import javax.inject.Inject

class AzkarRepository @Inject constructor(
    private val azkarDao: AzkarDao
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
    }
}
