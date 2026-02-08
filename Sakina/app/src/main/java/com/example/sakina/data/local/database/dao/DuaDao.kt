package com.example.sakina.data.local.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sakina.data.local.database.entity.DuaCategoryEntity
import com.example.sakina.data.local.database.entity.DuaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DuaDao {

    @Query("SELECT * FROM dua_categories")
    suspend fun getAllCategories(): List<DuaCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<DuaCategoryEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(duas: List<DuaEntity>)

    @Query("SELECT * FROM dua_table")
    fun getAllDuas(): Flow<List<DuaEntity>>

    @Query("SELECT * FROM dua_table WHERE categoryId = :catId")
    fun getDuasByCategory(catId: String): Flow<List<DuaEntity>>

    @Query("SELECT * FROM dua_table WHERE text LIKE '%' || :query || '%'")
    fun searchDuas(query: String): Flow<List<DuaEntity>>

    @Query("UPDATE dua_table SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFav: Boolean)

    @Query("SELECT * FROM dua_table WHERE isFavorite = 1")
    fun getFavoriteDuas(): Flow<List<DuaEntity>>

    @Query("SELECT COUNT(*) FROM dua_table")
    suspend fun getCount(): Int
}