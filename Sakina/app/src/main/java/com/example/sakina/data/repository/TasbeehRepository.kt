package com.example.sakina.data.repository

import com.example.sakina.data.local.database.dao.TasbeehDao
import com.example.sakina.data.local.database.entity.TasbeehEntity
import com.example.sakina.domain.model.Tasbeeh
import javax.inject.Inject

class TasbeehRepository @Inject constructor(
    private val dao: TasbeehDao
) {

    suspend fun getTasbeeh(): List<TasbeehEntity> {
        return dao.getAll()
    }

    suspend fun getTotalCount(): Int = dao.getTotalCount()

    suspend fun increment(id: Int) {
        dao.increment(id)
    }

    suspend fun insertDefaults(list: List<TasbeehEntity>) {
        dao.insertAll(list)
    }
}

