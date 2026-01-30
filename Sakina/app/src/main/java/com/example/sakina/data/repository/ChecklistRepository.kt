package com.example.sakina.data.repository

import com.example.sakina.data.local.database.dao.ChecklistDao
import com.example.sakina.data.local.database.entity.ChecklistEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChecklistRepository @Inject constructor(
    private val dao: ChecklistDao
) {


    val allTasks: Flow<List<ChecklistEntity>> = dao.getAllTasks()


    suspend fun addTask(task: ChecklistEntity) {
        dao.addTask(task)
    }


    suspend fun updateTaskStatus(task: ChecklistEntity) {
        dao.updateTaskStatus(task)
    }


    suspend fun deleteTask(task: ChecklistEntity) {
        dao.deleteTask(task)
    }
}
