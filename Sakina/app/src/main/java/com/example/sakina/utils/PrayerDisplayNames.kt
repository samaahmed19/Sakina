package com.example.sakina.utils

import com.example.sakina.domain.model.PrayerKey

fun prayerTitleAr(key: PrayerKey): String = when (key) {
    PrayerKey.PRAYER_FAJR -> "الفجر"
    PrayerKey.PRAYER_DHUHR -> "الظهر"
    PrayerKey.PRAYER_ASR -> "العصر"
    PrayerKey.PRAYER_MAGHRIB -> "المغرب"
    PrayerKey.PRAYER_ISHA -> "العشاء"
    PrayerKey.NAFILA_DUHA -> "الضحى"
    PrayerKey.NAFILA_WITR -> "الوتر"
    PrayerKey.NAFILA_QIYAM -> "قيام الليل"
}

