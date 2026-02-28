package com.sama.sakina

import com.sama.sakina.domain.model.Prayer
import com.sama.sakina.domain.model.PrayerDaySummary
import com.sama.sakina.domain.model.PrayerKey
import com.sama.sakina.domain.model.PrayerType
import com.sama.sakina.domain.model.ZawalStatus
import com.sama.sakina.domain.usecase.GetDayPrayersUseCase
import com.sama.sakina.data.repository.PrayerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GetDayPrayersUseCase].
 * Uses mockk to stub repository; no production code changes.
 */
class GetDayPrayersUseCaseTest {

    private val repository: PrayerRepository = mockk()
    private lateinit var useCase: GetDayPrayersUseCase

    private val testSummary = PrayerDaySummary(
        date = "2025-02-08",
        items = listOf(
            Prayer(PrayerKey.PRAYER_FAJR, "الفجر", PrayerType.FARD, false)
        ),
        completedFardCount = 0,
        totalFardCount = 5,
        isAllFardCompleted = false,
        shouldCelebrate = false,
        motivationalText = null,
        zawalStatus = ZawalStatus.Unknown
    )

    @Before
    fun setup() {
        useCase = GetDayPrayersUseCase(repository)
    }

    @Test
    fun invoke_callsRepositoryAndReturnsSummary() = runTest {
        coEvery { repository.getSummary("2025-02-08") } returns testSummary
        val result = useCase("2025-02-08")
        assertEquals(testSummary, result)
        assertEquals("2025-02-08", result.date)
    }
}
