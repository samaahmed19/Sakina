package com.example.sakina.domain.model

/**
 * Unique keys for each prayer / nafila.
 * Keep this enum in a single place to avoid duplicate declarations.
 */
enum class PrayerKey(val key: String) {
    PRAYER_FAJR("PRAYER_FAJR"),
    PRAYER_DHUHR("PRAYER_DHUHR"),
    PRAYER_ASR("PRAYER_ASR"),
    PRAYER_MAGHRIB("PRAYER_MAGHRIB"),
    PRAYER_ISHA("PRAYER_ISHA"),

    NAFILA_DUHA("NAFILA_DUHA"),
    NAFILA_WITR("NAFILA_WITR"),
    NAFILA_QIYAM("NAFILA_QIYAM")
}

enum class PrayerType {
    FARD,
    NAFILA
}

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