package com.example.sakina.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,

    val title: String,

    val icon: String
)

@Entity(
    tableName = "azkar",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class ZikrEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val categoryId: String,

    val text: String,

    val repeat: Int,

    val reward: String?
)
