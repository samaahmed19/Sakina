package com.example.sakina.domain.model

data class Dua(
    val id: Int,
    val text: String,
    val categoryId: Int,
    val categoryName: String,
    val isFavorite: Boolean
)