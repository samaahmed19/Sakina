package com.example.sakina.domain.model


data class ZikrCategory(
    val id: String,
    val title: String,
    val icon: String
)
data class Zikr(
    val id: Int,
    val categoryId: String,
    val text: String,
    val repeat: Int,
    val reward: String?
)