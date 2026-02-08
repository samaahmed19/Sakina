package com.example.sakina.data.local.database.entity


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "dua_categories")
data class DuaCategoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val icon: String,
    val count: Int
)

@Entity(
    tableName = "dua_table",
    foreignKeys = [
        ForeignKey(
            entity = DuaCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class DuaEntity(
    @PrimaryKey(autoGenerate = true)

    val id: Int = 0,
    val categoryId: String,
    val text: String,
    val isFavorite: Boolean = false
)