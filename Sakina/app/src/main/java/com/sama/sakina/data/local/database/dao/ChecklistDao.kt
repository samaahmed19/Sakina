package com.sama.sakina.data.local.database.dao

import androidx.room.*
import com.sama.sakina.data.local.database.entity.ChecklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("""
        SELECT * FROM checklist_tasks
        ORDER BY sortOrder ASC, createdAt DESC
    """)
    fun getAllTasks(): Flow<List<ChecklistEntity>>

    @Query("SELECT * FROM checklist_tasks")
    suspend fun getTasksOnce(): List<ChecklistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: ChecklistEntity)

    @Update
    suspend fun updateTask(task: ChecklistEntity)

    @Delete
    suspend fun deleteTask(task: ChecklistEntity)

    @Query("UPDATE checklist_tasks SET isCompleted = 0, completedAt = NULL, updatedAt = :updatedAt")
    suspend fun resetAllTasks(updatedAt: Long = System.currentTimeMillis())

    // ===== إضافات احترافية =====

    @Query("UPDATE checklist_tasks SET taskName = :newName, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskName(taskId: Int, newName: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE checklist_tasks SET category = :category, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskCategory(taskId: Int, category: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE checklist_tasks SET sortOrder = :sortOrder, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun updateTaskOrder(taskId: Int, sortOrder: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM checklist_tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()

    @Query("SELECT COUNT(*) FROM checklist_tasks WHERE isCompleted = 1")
    suspend fun getCompletedCount(): Int

    @Query("SELECT COUNT(*) FROM checklist_tasks")
    suspend fun getTotalCount(): Int
}