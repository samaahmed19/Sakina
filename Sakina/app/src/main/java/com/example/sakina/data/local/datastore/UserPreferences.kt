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
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val userName: Flow<String> =
        context.dataStore.data.map { it[USER_NAME] ?: "" }

    val userEmail: Flow<String> =
        context.dataStore.data.map { it[USER_EMAIL] ?: "" }

    val location: Flow<String> =
        context.dataStore.data.map { it[LOCATION] ?: "" }

    val isFirstLaunch: Flow<Boolean> =
        context.dataStore.data.map { it[FIRST_LAUNCH] ?: true }

    suspend fun saveUser(
        name: String,
        email: String,
        location: String
    ) {
        context.dataStore.edit {
            it[USER_NAME] = name
            it[USER_EMAIL] = email
            it[LOCATION] = location
            it[FIRST_LAUNCH] = false
        }
    }
}
