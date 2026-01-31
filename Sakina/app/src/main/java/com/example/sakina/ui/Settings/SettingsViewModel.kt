package com.example.sakina.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import com.example.sakina.data.local.datastore.UserPreferences

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    data class SettingsState(
        val name: String = "",
        val email: String = "",
        val location: String = "غير محدد",
        val prayerNotifications: Boolean = true,
        val azkarNotifications: Boolean = true,
        val darkMode: Boolean = false,
        val language: String = "العربية"
    )

    // Reactive StateFlow that automatically updates when DataStore changes
    val state: StateFlow<SettingsState> = dataStore.data
        .catch { exception ->
            // Handle exceptions safely
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            SettingsState(
                name = prefs[PreferencesKeys.NAME] ?: "",
                email = prefs[PreferencesKeys.EMAIL] ?: "",
                location = prefs[PreferencesKeys.LOCATION] ?: "غير محدد",
                prayerNotifications = prefs[PreferencesKeys.PRAYER_NOTIF] ?: true,
                azkarNotifications = prefs[PreferencesKeys.AZKAR_NOTIF] ?: true,
                darkMode = prefs[PreferencesKeys.DARK_MODE] ?: false,
                language = prefs[PreferencesKeys.LANGUAGE] ?: "العربية"
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            SettingsState()
        )

    // Generic function to save any preference
    private fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            dataStore.edit { it[key] = value }
        }
    }

    // Update functions
    fun updatePrayerNotifications(value: Boolean) =
        savePreference(PreferencesKeys.PRAYER_NOTIF, value)

    fun updateAzkarNotifications(value: Boolean) =
        savePreference(PreferencesKeys.AZKAR_NOTIF, value)

    fun updateDarkMode(value: Boolean) =
        savePreference(PreferencesKeys.DARK_MODE, value)

    fun updateLanguage(value: String) =
        savePreference(PreferencesKeys.LANGUAGE, value)

    fun updateLocation(value: String) =
        savePreference(PreferencesKeys.LOCATION, value)
}