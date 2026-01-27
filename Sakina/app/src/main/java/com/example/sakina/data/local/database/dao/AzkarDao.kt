package com.example.sakina.data.local.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sakina.data.local.database.entity.ZikrEntity
import com.example.sakina.data.local.database.entity.CategoryEntity
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
}
