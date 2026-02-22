package com.sama.sakina.domain.usecase

import com.sama.sakina.data.repository.PrayerRepository
import com.sama.sakina.domain.model.PrayerDaySummary
import javax.inject.Inject

class GetDayPrayersUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke(date: String): PrayerDaySummary {
        return repository.getSummary(date)
    }
}
