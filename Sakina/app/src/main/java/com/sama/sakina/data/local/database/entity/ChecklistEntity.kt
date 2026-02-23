package com.sama.sakina.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_tasks")
data class ChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskName: String,

    // الجديد
    val category: String = "عام",
    val sortOrder: Int = 0,

    val isCompleted: Boolean = false,
    val completedAt: Long? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)