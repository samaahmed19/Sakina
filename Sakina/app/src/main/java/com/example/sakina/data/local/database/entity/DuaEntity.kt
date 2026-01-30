package com.example.sakina.data.local.database.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dua_table")
data class DuaEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val text: String,
    val categoryId: Int,
    val categoryName: String,
    val isFavorite: Boolean = false
)