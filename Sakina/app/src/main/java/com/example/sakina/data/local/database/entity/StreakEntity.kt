package com.example.sakina.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak")
data class StreakEntity(
    @PrimaryKey val id: Int = 0,
    val streakDays: Int,
    val lastCompletedDay: Long
)
