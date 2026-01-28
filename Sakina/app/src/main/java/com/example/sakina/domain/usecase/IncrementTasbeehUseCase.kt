package com.example.sakina.domain.usecase

import javax.inject.Inject
import com.example.sakina.data.repository.TasbeehRepository

class IncrementTasbeehUseCase @Inject constructor(
    private val repo: TasbeehRepository
) {
    suspend operator fun invoke(id: Int) {
        repo.increment(id)
    }
}


