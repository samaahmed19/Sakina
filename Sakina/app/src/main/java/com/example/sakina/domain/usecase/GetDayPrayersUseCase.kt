package com.example.sakina.domain.usecase

import com.example.sakina.data.repository.PrayerRepository
import com.example.sakina.domain.model.PrayerDaySummary
import javax.inject.Inject

class GetDayPrayersUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke(date: String): PrayerDaySummary {
        return repository.getSummary(date)
    }
}
