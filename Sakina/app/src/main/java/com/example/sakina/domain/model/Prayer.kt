package com.example.sakina.domain.model

data class Prayer(
    val key: PrayerKey,
    val titleAr: String,
    val type: PrayerType,
    val isCompleted: Boolean
)

sealed class ZawalStatus {
    data object Unknown : ZawalStatus()
}

data class PrayerDaySummary(
    val date: String,
    val items: List<Prayer>,
    val completedFardCount: Int,
    val totalFardCount: Int,
    val isAllFardCompleted: Boolean,
    val shouldCelebrate: Boolean,
    val motivationalText: String?,
    val zawalStatus: ZawalStatus
)