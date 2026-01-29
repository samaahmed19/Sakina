package com.example.sakina.domain.usecase

import com.example.sakina.domain.model.Prayer
import com.example.sakina.domain.model.PrayerType
import javax.inject.Inject


class ShouldCelebrateUseCase @Inject constructor() {

    operator fun invoke(prayers: List<Prayer>): Boolean {
        return prayers
            .filter { it.type == PrayerType.FARD }
            .all { it.isCompleted }
    }
}
