package com.example.sakina.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {

    // User info
    val NAME = stringPreferencesKey("name")
    val EMAIL = stringPreferencesKey("email")
    val LOCATION = stringPreferencesKey("location")

    // Notifications
    val PRAYER_NOTIF = booleanPreferencesKey("prayer_notifications")
    val AZKAR_NOTIF = booleanPreferencesKey("azkar_notifications")

    // Appearance & language
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val LANGUAGE = stringPreferencesKey("language")
}
