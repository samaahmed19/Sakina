package com.example.sakina.data.repository

import com.example.sakina.data.local.database.dao.DuaDao
import com.example.sakina.data.local.database.entity.DuaCategoryEntity
import com.example.sakina.data.local.database.entity.DuaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DuaRepository @Inject constructor(
    private val duaDao: DuaDao
) {

    fun getAllDuas(): Flow<List<DuaEntity>> {
        return duaDao.getAllDuas()
    }

    suspend fun getAllCategories(): List<DuaCategoryEntity> {
        return duaDao.getAllCategories()
    }

    fun getDuasByCategory(categoryId: String): Flow<List<DuaEntity>> {
        return duaDao.getDuasByCategory(categoryId)
    }

    suspend fun insertAllDuas(duas: List<DuaEntity>) {
        duaDao.insertAll(duas)
    }


    suspend fun getCount(): Int {
        return duaDao.getCount()
    }


    fun searchDuas(query: String): Flow<List<DuaEntity>> {
        return duaDao.searchDuas(query)
    }

    fun getFavoriteDuas(): Flow<List<DuaEntity>> {
        return duaDao.getFavoriteDuas()
    }


    suspend fun updateFavorite(id: Int, isFavorite: Boolean) {
        duaDao.updateFavorite(id, isFavorite)
    }
}