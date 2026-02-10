package com.example.sakina.ui.Prayers

import com.example.sakina.domain.model.PrayerDaySummary
import com.example.sakina.domain.model.PrayerKey
import com.example.sakina.domain.model.PrayerSettings

data class PrayerUiState(
    val isLoading: Boolean = false,
    val summary: PrayerDaySummary? = null,
    val error: String? = null,
    val settings: PrayerSettings = PrayerSettings(),
    val fardPrayerTimes: Map<PrayerKey, Long> = emptyMap(),
    val nextFardPrayerKey: PrayerKey? = null,
    val nextFardPrayerTimeMillis: Long? = null
)
