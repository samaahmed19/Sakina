package com.example.sakina.domain.usecase



import com.example.sakina.data.repository.PrayerRepository
import com.example.sakina.domain.model.PrayerDaySummary
import com.example.sakina.domain.model.PrayerKey
import javax.inject.Inject

class CompletePrayerUseCase @Inject constructor(
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