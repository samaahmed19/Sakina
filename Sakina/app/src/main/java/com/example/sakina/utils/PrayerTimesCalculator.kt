package com.example.sakina.utils

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.example.sakina.domain.model.PrayerKey
import com.example.sakina.domain.model.PrayerMadhab
import com.example.sakina.domain.model.PrayerSettings
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTimesCalculator @Inject constructor() {

    data class FardTimes(
        val fajrMillis: Long,
        val dhuhrMillis: Long,
        val asrMillis: Long,
        val maghribMillis: Long,
        val ishaMillis: Long
    ) {
        fun asMap(): Map<PrayerKey, Long> = mapOf(
            PrayerKey.PRAYER_FAJR to fajrMillis,
            PrayerKey.PRAYER_DHUHR to dhuhrMillis,
            PrayerKey.PRAYER_ASR to asrMillis,
            PrayerKey.PRAYER_MAGHRIB to maghribMillis,
            PrayerKey.PRAYER_ISHA to ishaMillis
        )
    }

    fun calculateFardTimes(
        latitude: Double,
        longitude: Double,
        date: Calendar,
        settings: PrayerSettings,
        timeZone: TimeZone = TimeZone.getDefault()
    ): FardTimes {
        val calendar = (date.clone() as Calendar).apply {
            this.timeZone = timeZone
        }

        val coordinates = Coordinates(latitude, longitude)
        val dateComponents = DateComponents.from(calendar.time)

        val params = calculationParametersFor(settings)
        val prayerTimes = PrayerTimes(coordinates, dateComponents, params)

        return FardTimes(
            fajrMillis = prayerTimes.fajr.time,
            dhuhrMillis = prayerTimes.dhuhr.time,
            asrMillis = prayerTimes.asr.time,
            maghribMillis = prayerTimes.maghrib.time,
            ishaMillis = prayerTimes.isha.time
        )
    }

    private fun calculationParametersFor(settings: PrayerSettings): CalculationParameters {
        val method = runCatching { CalculationMethod.valueOf(settings.method.name) }.getOrDefault(CalculationMethod.EGYPTIAN)
        val params = method.parameters

        params.madhab = when (settings.madhab) {
            PrayerMadhab.SHAFI -> Madhab.SHAFI
            PrayerMadhab.HANAFI -> Madhab.HANAFI
        }

        return params
    }
}
