package com.example.sakina.data.repository

import com.example.sakina.data.local.database.dao.TasbeehDao
import com.example.sakina.data.local.database.entity.TasbeehEntity
import com.example.sakina.data.source.mapper.TasbeehMapper
import com.example.sakina.domain.model.Tasbeeh
import javax.inject.Inject

class TasbeehRepository @Inject constructor(
    private val dao: TasbeehDao
) {

    suspend fun getTasbeeh(): List<Tasbeeh> {
        return dao.getAll().map {
            TasbeehMapper.entityToDomain(it)
        }
    }

    suspend fun increment(id: Int) {
        dao.increment(id)
    }

    suspend fun insertDefaults(list: List<TasbeehEntity>) {
        dao.insertAll(list)
    }
}
