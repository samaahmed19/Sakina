package com.example.sakina

import com.example.sakina.domain.model.Prayer
import com.example.sakina.domain.model.PrayerKey
import com.example.sakina.domain.model.PrayerType
import com.example.sakina.domain.usecase.ShouldCelebrateUseCase
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShouldCelebrateUseCaseTest {

    private val useCase = ShouldCelebrateUseCase()

    @Test
    fun celebrates_only_when_all_fard_completed() {
        val fardDone = listOf(
            Prayer(PrayerKey.PRAYER_FAJR, "الفجر", PrayerType.FARD, true),
            Prayer(PrayerKey.PRAYER_DHUHR, "الظهر", PrayerType.FARD, true),
            Prayer(PrayerKey.PRAYER_ASR, "العصر", PrayerType.FARD, true),
            Prayer(PrayerKey.PRAYER_MAGHRIB, "المغرب", PrayerType.FARD, true),
            Prayer(PrayerKey.PRAYER_ISHA, "العشاء", PrayerType.FARD, true),
        )
        assertTrue(useCase(fardDone))

        val oneMissing = fardDone.toMutableList().apply {
            this[2] = this[2].copy(isCompleted = false) // ASR false
        }
        assertFalse(useCase(oneMissing))

        // Nawafil shouldn't affect celebration
        val withNawafil = fardDone + Prayer(PrayerKey.NAFILA_WITR, "الوتر", PrayerType.NAFILA, false)
        assertTrue(useCase(withNawafil))
    }
}
