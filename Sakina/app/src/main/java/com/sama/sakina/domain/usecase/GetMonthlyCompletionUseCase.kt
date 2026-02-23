package com.sama.sakina.domain.usecase

import com.sama.sakina.data.repository.PrayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyCompletionUseCase @Inject constructor(
    private val repository: PrayerRepository
) {
    operator fun invoke(month: String): Flow<Map<String, Int>> {
        return repository.getMonthlyCompletion(month)
    }
}