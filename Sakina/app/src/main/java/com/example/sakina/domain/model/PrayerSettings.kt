package com.example.sakina.domain.model

enum class PrayerCalculationMethod(val labelAr: String) {
    EGYPTIAN("الهيئة المصرية"),
    MUSLIM_WORLD_LEAGUE("رابطة العالم الإسلامي"),
    UMM_AL_QURA("أم القرى"),
    KARACHI("كراتشي"),
    NORTH_AMERICA("أمريكا الشمالية"),
    DUBAI("دبي"),
    QATAR("قطر"),
    KUWAIT("الكويت"),
    MOONSIGHTING_COMMITTEE("لجنة رؤية الهلال"),
    SINGAPORE("سنغافورة"),
    TURKEY("تركيا"),
    TEHRAN("طهران")
}

enum class PrayerMadhab(val labelAr: String) {
    SHAFI("شافعي"),
    HANAFI("حنفي")
}

data class PrayerSettings(
    val method: PrayerCalculationMethod = PrayerCalculationMethod.EGYPTIAN,
    val madhab: PrayerMadhab = PrayerMadhab.SHAFI
)

