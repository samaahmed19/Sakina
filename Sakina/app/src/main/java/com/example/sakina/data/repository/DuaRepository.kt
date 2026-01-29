package com.example.sakina.data.local.repository

import com.example.sakina.data.local.database.dao.DuaDao
import com.example.sakina.data.local.database.entity.DuaEntity
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class DuaRepository @Inject constructor(
    private val duaDao: DuaDao
) {
    suspend fun insertAllDuas(duas: List<DuaEntity>) {
        duaDao.insertAll(duas)
    }

    suspend fun getCount(): Int {
        return duaDao.getCount()
    }

    fun getDuasByCategory(categoryId: Int): Flow<List<DuaEntity>> {
        return duaDao.getDuasByCategory(categoryId)
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