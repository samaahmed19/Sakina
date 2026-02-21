package com.sama.sakina.data.repository

import com.sama.sakina.data.local.database.dao.TasbeehDao
import com.sama.sakina.data.local.database.entity.TasbeehEntity
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

