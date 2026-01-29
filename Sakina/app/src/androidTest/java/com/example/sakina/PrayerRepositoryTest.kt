package com.example.sakina

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.sakina.data.repository.PrayerRepository
import com.example.sakina.domain.model.PrayerKey
import com.example.sakina.domain.usecase.ShouldCelebrateUseCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PrayerRepositoryTest {

    private lateinit var db: PrayerTestDatabase
    private lateinit var repo: PrayerRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PrayerTestDatabase::class.java
        ).allowMainThreadQueries().build()

        repo = PrayerRepository(
            prayerDao = db.prayerDao(),
            shouldCelebrateUseCase = ShouldCelebrateUseCase()
        )

    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun summary_initially_not_celebrating() = runBlocking {
        val date = "2026-01-29"
        val summary = repo.getSummary(date)

        assertEquals(0, summary.completedFardCount)
        assertFalse(summary.isAllFardCompleted)
        assertFalse(summary.shouldCelebrate)
    }

    @Test
    fun celebrate_only_when_all_5_fard_completed() = runBlocking {
        val date = "2026-01-29"

        repo.setCompleted(date, PrayerKey.PRAYER_FAJR, true)
        repo.setCompleted(date, PrayerKey.PRAYER_DHUHR, true)
        repo.setCompleted(date, PrayerKey.PRAYER_ASR, true)
        repo.setCompleted(date, PrayerKey.PRAYER_MAGHRIB, true)

        val almost = repo.getSummary(date)
        assertFalse(almost.shouldCelebrate)

        repo.setCompleted(date, PrayerKey.PRAYER_ISHA, true)
        val done = repo.getSummary(date)

        assertTrue(done.isAllFardCompleted)
        assertTrue(done.shouldCelebrate)

        // uncheck one -> shouldCelebrate false again
        repo.setCompleted(date, PrayerKey.PRAYER_ASR, false)
        val after = repo.getSummary(date)
        assertFalse(after.shouldCelebrate)
    }
}
