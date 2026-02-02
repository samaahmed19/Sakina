package com.example.sakina.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    // State

    data class SettingsState(
        val name: String = "",
        val email: String = "",
        val location: String = "غير محدد",
        val prayerNotifications: Boolean = true,
        val azkarNotifications: Boolean = true,
        val darkMode: Boolean = false,
        val language: String = "العربية"
    )

    // Combine all DataStore flows into one UI state
    val state: StateFlow<SettingsState> = combine(
        userPreferences.userName,
        userPreferences.userEmail,
        userPreferences.location,
        userPreferences.prayerNotifications,
        userPreferences.azkarNotifications,
        userPreferences.darkMode,
        userPreferences.language
    ) { name, email, location, prayer, azkar, dark, lang ->
        SettingsState(
            name = name,
            email = email,
            location = location,
            prayerNotifications = prayer,
            azkarNotifications = azkar,
            darkMode = dark,
            language = lang
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsState()
    )

    // Update functions

    fun updateName(value: String) {
        viewModelScope.launch {
            userPreferences.saveUserName(value)
        }
    }

    fun updateLocation(value: String) {
        viewModelScope.launch {
            userPreferences.saveLocation(value)
        }
    }

    fun updatePrayerNotifications(value: Boolean) {
        viewModelScope.launch {
            userPreferences.setPrayerNotifications(value)
        }
    }

    fun updateAzkarNotifications(value: Boolean) {
        viewModelScope.launch {
            userPreferences.setAzkarNotifications(value)
        }
    }

    fun updateDarkMode(value: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(value)
        }
    }

    fun updateLanguage(value: String) {
        viewModelScope.launch {
            userPreferences.setLanguage(value)
        }
    }
}
