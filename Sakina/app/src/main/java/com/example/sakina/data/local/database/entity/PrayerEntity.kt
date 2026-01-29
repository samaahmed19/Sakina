package com.example.sakina.data.local.database.entity

import androidx.room.Entity

@Entity(
    tableName = "prayer_completion",
    primaryKeys = ["date", "key"]
)
data class PrayerEntity(
    val date: String,
    val key: String,
    val isCompleted: Boolean,
    val completedAt: Long?
)
