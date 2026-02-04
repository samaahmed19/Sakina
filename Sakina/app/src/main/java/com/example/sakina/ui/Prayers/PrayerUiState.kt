package com.example.sakina.ui.Prayers

import com.example.sakina.domain.model.PrayerDaySummary

data class PrayerUiState(
    val isLoading: Boolean = false,
    val summary: PrayerDaySummary? = null,
    val error: String? = null
)
