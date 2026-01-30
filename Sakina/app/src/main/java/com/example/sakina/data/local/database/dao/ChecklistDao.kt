package com.example.sakina.data.local.database.dao

import androidx.room.*
import com.example.sakina.data.local.database.entity.ChecklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<ChecklistEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: ChecklistEntity)

    @Update
    suspend fun updateTaskStatus(task: ChecklistEntity)

    @Delete
    suspend fun deleteTask(task: ChecklistEntity)
}