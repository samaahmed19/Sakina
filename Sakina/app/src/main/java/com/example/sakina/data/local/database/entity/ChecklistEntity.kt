package com.example.sakina.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_tasks")
data class ChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskName: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),

    val lastStreakDay: Long = -1L,
    val streakDays: Int = 0
)