package com.example.sakina.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.sakina.domain.model.PrayerCalculationMethod
import com.example.sakina.domain.model.PrayerMadhab
import com.example.sakina.domain.model.PrayerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val methodKey = stringPreferencesKey("prayer_calc_method")
    private val madhabKey = stringPreferencesKey("prayer_madhab")

    val settingsFlow: Flow<PrayerSettings> = dataStore.data.map { prefs ->
        val method = prefs[methodKey]
            ?.let { runCatching { PrayerCalculationMethod.valueOf(it) }.getOrNull() }
            ?: PrayerSettings().method

        val madhab = prefs[madhabKey]
            ?.let { runCatching { PrayerMadhab.valueOf(it) }.getOrNull() }
            ?: PrayerSettings().madhab

        PrayerSettings(method = method, madhab = madhab)
    }

    suspend fun getOnce(): PrayerSettings = settingsFlow.first()

    suspend fun setMethod(method: PrayerCalculationMethod) {
        dataStore.edit { prefs -> prefs[methodKey] = method.name }
    }

    suspend fun setMadhab(madhab: PrayerMadhab) {
        dataStore.edit { prefs -> prefs[madhabKey] = madhab.name }
    }
}

