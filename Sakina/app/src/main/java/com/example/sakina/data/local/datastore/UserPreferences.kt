package com.example.sakina.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val LOCATION = stringPreferencesKey("location")
        val PRAYER_NOTIF = booleanPreferencesKey("prayer_notifications")
        val AZKAR_NOTIF = booleanPreferencesKey("azkar_notifications")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    //Flows

    val userName: Flow<String> =
        context.dataStore.data.map { it[USER_NAME] ?: "" }

    val userEmail: Flow<String> =
        context.dataStore.data.map { it[USER_EMAIL] ?: "" }

    val location: Flow<String> =
        context.dataStore.data.map { it[LOCATION] ?: "غير محدد" }

    val prayerNotifications: Flow<Boolean> =
        context.dataStore.data.map { it[PRAYER_NOTIF] ?: true }

    val azkarNotifications: Flow<Boolean> =
        context.dataStore.data.map { it[AZKAR_NOTIF] ?: true }

    val darkMode: Flow<Boolean> =
        context.dataStore.data.map { it[DARK_MODE] ?: false }

    val language: Flow<String> =
        context.dataStore.data.map { it[LANGUAGE] ?: "العربية" }

    val isFirstLaunch: Flow<Boolean> =
        context.dataStore.data.map { it[FIRST_LAUNCH] ?: true }

    // Save functions

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { it[USER_NAME] = name }
    }

    suspend fun saveLocation(value: String) {
        context.dataStore.edit { it[LOCATION] = value }
    }

    suspend fun setPrayerNotifications(value: Boolean) {
        context.dataStore.edit { it[PRAYER_NOTIF] = value }
    }

    suspend fun setAzkarNotifications(value: Boolean) {
        context.dataStore.edit { it[AZKAR_NOTIF] = value }
    }

    suspend fun setDarkMode(value: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = value }
    }

    suspend fun setLanguage(value: String) {
        context.dataStore.edit { it[LANGUAGE] = value }
    }

    suspend fun setFirstLaunch(value: Boolean) {
        context.dataStore.edit { it[FIRST_LAUNCH] = value }
    }
}
