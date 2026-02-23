package com.sama.sakina.data.local.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sama.sakina.data.local.database.entity.ZikrEntity
import com.sama.sakina.data.local.database.entity.CategoryEntity
 @Dao
interface AzkarDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): CategoryEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM azkar WHERE categoryId = :categoryId")
    suspend fun getAzkarByCategory(categoryId: String): List<ZikrEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAzkar(azkar: List<ZikrEntity>)

     @Query("UPDATE azkar SET currentCount = 0 WHERE lastUpdated < :todayStart")
     suspend fun resetOldAzkar(todayStart: Long)

     @Query("UPDATE azkar SET currentCount = :count, lastUpdated = :timestamp WHERE id = :zikrId")
     suspend fun updateZikrWithTimestamp(zikrId: Int, count: Int, timestamp: Long)

     @Query("UPDATE azkar SET currentCount = 0 WHERE categoryId = :categoryId")
     suspend fun resetCategoryCount(categoryId: String)
}
