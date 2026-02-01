package com.example.sakina.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.datastore.PreferencesKeys
import com.example.sakina.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val userRepository: UserRepository
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

    // دمج بيانات المستخدم من Room مع إعدادات DataStore
    val state: StateFlow<SettingsState> = combine(
        userRepository.getUser(),
        dataStore.data.catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
    ) { user, prefs ->
        SettingsState(
            name = user?.name?.takeIf { it.isNotBlank() } ?: (prefs[PreferencesKeys.NAME] ?: ""),
            email = user?.email?.takeIf { it.isNotBlank() } ?: (prefs[PreferencesKeys.EMAIL] ?: ""),
            location = (prefs[PreferencesKeys.LOCATION]?.takeIf { it.isNotBlank() } ?: user?.location?.takeIf { it.isNotBlank() }) ?: "غير محدد",
            prayerNotifications = prefs[PreferencesKeys.PRAYER_NOTIF] ?: true,
            azkarNotifications = prefs[PreferencesKeys.AZKAR_NOTIF] ?: true,
            darkMode = prefs[PreferencesKeys.DARK_MODE] ?: false,
            language = prefs[PreferencesKeys.LANGUAGE] ?: "العربية"
        )
    }.stateIn(
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

    fun updateLocation(value: String) {
        savePreference(PreferencesKeys.LOCATION, value)
    }
}