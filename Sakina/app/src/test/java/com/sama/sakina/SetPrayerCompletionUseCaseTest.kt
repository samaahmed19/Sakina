package com.sama.sakina

import com.sama.sakina.domain.model.Prayer
import com.sama.sakina.domain.model.PrayerDaySummary
import com.sama.sakina.domain.model.PrayerKey
import com.sama.sakina.domain.model.PrayerType
import com.sama.sakina.domain.model.ZawalStatus
import com.sama.sakina.domain.usecase.SetPrayerCompletionUseCase
import com.sama.sakina.data.repository.PrayerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [SetPrayerCompletionUseCase].
 * Uses mockk to stub repository; no production code changes.
 */
class SetPrayerCompletionUseCaseTest {

    private val repository: PrayerRepository = mockk()
    private lateinit var useCase: SetPrayerCompletionUseCase

    private val updatedSummary = PrayerDaySummary(
        date = "2025-02-08",
        items = listOf(
            Prayer(PrayerKey.PRAYER_FAJR, "الفجر", PrayerType.FARD, true)
        ),
        completedFardCount = 1,
        totalFardCount = 5,
        isAllFardCompleted = false,
        shouldCelebrate = false,
        motivationalText = null,
        zawalStatus = ZawalStatus.Unknown
    )

    @Before
    fun setup() {
        useCase = SetPrayerCompletionUseCase(repository)
    }

    @Test
    fun invoke_callsRepositorySetCompletedAndReturnsSummary() = runTest {
        coEvery {
            repository.setCompleted("2025-02-08", PrayerKey.PRAYER_FAJR, true)
        } returns updatedSummary
        val result = useCase("2025-02-08", PrayerKey.PRAYER_FAJR, true)
        assertEquals(updatedSummary, result)
        assertEquals(1, result.completedFardCount)
    }
}
