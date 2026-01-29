package com.example.sakina.domain.model
import com.example.sakina.domain.model.Tasbeeh

data class Tasbeeh(
    val id: Int,
    val slug: String,
    val text: String,
    val targets: List<Int>,
    val category: String,
    val virtue: String,
    val source: String,
    val priority: Int,
    val isDefault: Boolean,
    val currentCount: Int = 0
)
