package com.example.sakina.data.repository

import com.example.sakina.data.local.database.dao.PrayerDao
import com.example.sakina.data.local.database.entity.PrayerEntity
import com.example.sakina.domain.model.*
import com.example.sakina.domain.usecase.ShouldCelebrateUseCase
import javax.inject.Inject

class PrayerRepository @Inject constructor(
    private val prayerDao: PrayerDao,
    private val shouldCelebrateUseCase: ShouldCelebrateUseCase
) {

    // Definitions (can move to JSON later if UI/UX wants)
    private val fard: List<Pair<PrayerKey, String>> = listOf(
        PrayerKey.PRAYER_FAJR to "الفجر",
        PrayerKey.PRAYER_DHUHR to "الظهر",
        PrayerKey.PRAYER_ASR to "العصر",
        PrayerKey.PRAYER_MAGHRIB to "المغرب",
        PrayerKey.PRAYER_ISHA to "العشاء",
    )

    private val nawafil: List<Pair<PrayerKey, String>> = listOf(
        PrayerKey.NAFILA_DUHA to "الضحى",
        PrayerKey.NAFILA_WITR to "الوتر",
        PrayerKey.NAFILA_QIYAM to "قيام الليل",
    )

    suspend fun getSummary(date: String): PrayerDaySummary {
        val rows: List<PrayerEntity> = prayerDao.getByDate(date)

        // Fast lookup: "PRAYER_FAJR" -> true/false
        val completedMap: Map<String, Boolean> =
            rows.associate { row -> row.key to row.isCompleted }

        val fardItems: List<Prayer> = fard.map { (key, title) ->
            Prayer(
                key = key,
                titleAr = title,
                type = PrayerType.FARD,
                isCompleted = completedMap[key.key] ?: false
            )
        }

        val nafilaItems: List<Prayer> = nawafil.map { (key, title) ->
            Prayer(
                key = key,
                titleAr = title,
                type = PrayerType.NAFILA,
                isCompleted = completedMap[key.key] ?: false
            )
        }

        val items = fardItems + nafilaItems

        val completedFardCount = fardItems.count { it.isCompleted }
        val totalFardCount = fardItems.size
        val isAllFardCompleted = completedFardCount == totalFardCount

        val shouldCelebrate = isAllFardCompleted

        return PrayerDaySummary(
            date = date,
            items = fardItems + nafilaItems,
            completedFardCount = completedFardCount,
            totalFardCount = totalFardCount,
            isAllFardCompleted = isAllFardCompleted,
            shouldCelebrate = shouldCelebrate,
            motivationalText = if (shouldCelebrate) "بارك الله فيك! كملتي صلاتك اليوم." else null,
            zawalStatus = ZawalStatus.Unknown
        )
    }

    suspend fun setCompleted(
        date: String,
        key: PrayerKey,
        isCompleted: Boolean
    ): PrayerDaySummary {
        prayerDao.upsert(
            PrayerEntity(
                date = date,
                key = key.key,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) System.currentTimeMillis() else null
            )
        )
        return getSummary(date)
    }
}