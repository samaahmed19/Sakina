package com.example.sakina.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasbeeh")
data class TasbeehEntity(
    @PrimaryKey
    val id: Int,
    val slug: String,
    val text: String,
    val targets: String, // JSON Array مخزن كنص
    val category: String,
    val virtue: String,
    val source: String,
    val priority: Int,
    val isDefault: Boolean,
    val currentCount: Int
)