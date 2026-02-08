package com.example.sakina.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sakina.data.local.database.entity.ChecklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {

    @Query("SELECT * FROM checklist_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<ChecklistEntity>>

    // Used by DailyResetWorker
    @Query("UPDATE checklist_tasks SET isCompleted = 0")
    suspend fun resetAllTasks()

    // Optional – keep only if you really need it
    @Query("SELECT * FROM checklist_tasks")
    suspend fun getTasksOnce(): List<ChecklistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: ChecklistEntity)

    @Update
    suspend fun updateTask(task: ChecklistEntity)

    @Update
    suspend fun updateTaskStatus(task: ChecklistEntity)

    @Delete
    suspend fun deleteTask(task: ChecklistEntity)
}
