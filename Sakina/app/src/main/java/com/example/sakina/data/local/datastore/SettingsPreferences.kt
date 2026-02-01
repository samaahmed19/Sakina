package com.example.sakina.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings_prefs")

@Singleton
class SettingsPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    object Keys {
        val PRAYER_NOTIF = booleanPreferencesKey("prayer_notifications")
        val AZKAR_NOTIF = booleanPreferencesKey("azkar_notifications")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val LOCATION = stringPreferencesKey("location")
    }

    suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    fun <T> read(
        key: Preferences.Key<T>,
        default: T
    ): Flow<T> {
        return context.dataStore.data.map { prefs ->
            prefs[key] ?: default
        }
    }
}
