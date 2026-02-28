package com.sama.sakina

import com.sama.sakina.domain.model.Prayer
import com.sama.sakina.domain.model.PrayerKey
import com.sama.sakina.domain.model.PrayerType
import com.sama.sakina.domain.usecase.ShouldCelebrateUseCase
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [ShouldCelebrateUseCase].
 * Covers celebration logic for Fard prayers without changing production code.
 */
class ShouldCelebrateUseCaseTest {

    private lateinit var useCase: ShouldCelebrateUseCase

    @Before
    fun setup() {
        useCase = ShouldCelebrateUseCase()
    }

    @Test
    fun allFardCompleted_returnsTrue() {
        val prayers = listOf(
            prayer(PrayerKey.PRAYER_FAJR, true),
            prayer(PrayerKey.PRAYER_DHUHR, true),
            prayer(PrayerKey.PRAYER_ASR, true),
            prayer(PrayerKey.PRAYER_MAGHRIB, true),
            prayer(PrayerKey.PRAYER_ISHA, true)
        )
        assertTrue(useCase(prayers))
    }

    @Test
    fun oneFardNotCompleted_returnsFalse() {
        val prayers = listOf(
            prayer(PrayerKey.PRAYER_FAJR, true),
            prayer(PrayerKey.PRAYER_DHUHR, false),
            prayer(PrayerKey.PRAYER_ASR, true)
        )
        assertFalse(useCase(prayers))
    }

    @Test
    fun emptyList_returnsTrue() {
        assertTrue(useCase(emptyList()))
    }

    @Test
    fun onlyNafilaCompleted_fardNotCounted_returnsTrue() {
        val prayers = listOf(
            prayer(PrayerKey.NAFILA_DUHA, true),
            prayer(PrayerKey.NAFILA_WITR, true)
        )
        assertTrue(useCase(prayers))
    }

    @Test
    fun nafilaIncomplete_doesNotAffectResult() {
        val prayers = listOf(
            prayer(PrayerKey.PRAYER_FAJR, true),
            prayer(PrayerKey.PRAYER_DHUHR, true),
            prayer(PrayerKey.NAFILA_DUHA, false)
        )
        assertTrue(useCase(prayers))
    }

    private fun prayer(key: PrayerKey, isCompleted: Boolean) = Prayer(
        key = key,
        titleAr = key.key,
        type = if (key.name.startsWith("PRAYER_")) PrayerType.FARD else PrayerType.NAFILA,
        isCompleted = isCompleted
    )
}
