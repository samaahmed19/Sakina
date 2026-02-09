package com.example.sakina.data.local.database.dao

import androidx.room.*
import com.example.sakina.data.local.database.entity.ChecklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<ChecklistEntity>>

    @Query("SELECT * FROM checklist_tasks")
    suspend fun getTasksOnce(): List<ChecklistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: ChecklistEntity)

    @Update
    suspend fun updateTask(task: ChecklistEntity)

    @Delete
    suspend fun deleteTask(task: ChecklistEntity)

    // الدالة الضرورية لتشغيل الـ Worker
    @Query("UPDATE checklist_tasks SET isCompleted = 0")
    suspend fun resetAllTasks()
}