package com.sama.sakina.domain.usecase

import com.sama.sakina.data.repository.PrayerRepository
import com.sama.sakina.domain.model.PrayerDaySummary
import com.sama.sakina.domain.model.PrayerKey
import javax.inject.Inject

class SetPrayerCompletionUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    suspend operator fun invoke(
        date: String,
        key: PrayerKey,
        isCompleted: Boolean
    ): PrayerDaySummary {
        return repository.setCompleted(date, key, isCompleted)
    }
}
