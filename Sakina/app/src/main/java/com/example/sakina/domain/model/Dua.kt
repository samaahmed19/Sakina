package com.example.sakina.domain.model

data class DuaCategory(
    val id: String,
    val title: String,
    val icon: String,
)
data class Dua(
    val id: Int = 0,
    val categoryId: String,
    val text: String,
    val isFavorite: Boolean = false
)