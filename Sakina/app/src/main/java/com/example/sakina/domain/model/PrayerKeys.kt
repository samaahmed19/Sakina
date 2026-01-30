package com.example.sakina.domain.model

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

enum class PrayerType { FARD, NAFILA }