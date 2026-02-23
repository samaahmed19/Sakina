package com.sama.sakina.ui.Prayers

import com.sama.sakina.domain.model.PrayerDaySummary
import com.sama.sakina.domain.model.PrayerKey
import com.sama.sakina.domain.model.PrayerSettings

data class PrayerUiState(
    val isLoading: Boolean = false,
    val summary: PrayerDaySummary? = null,
    val error: String? = null,
    val settings: PrayerSettings = PrayerSettings(),
    val fardPrayerTimes: Map<PrayerKey, Long> = emptyMap(),
    val monthlyCompletion: Map<String, Int> = emptyMap(),
    val nextFardPrayerKey: PrayerKey? = null,
    val nextFardPrayerTimeMillis: Long? = null
)
