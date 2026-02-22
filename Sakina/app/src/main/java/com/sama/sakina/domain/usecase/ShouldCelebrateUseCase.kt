package com.sama.sakina.domain.usecase

import com.sama.sakina.domain.model.Prayer
import com.sama.sakina.domain.model.PrayerType
import javax.inject.Inject


class ShouldCelebrateUseCase @Inject constructor() {

    operator fun invoke(prayers: List<Prayer>): Boolean {
        return prayers
            .filter { it.type == PrayerType.FARD }
            .all { it.isCompleted }
    }
}