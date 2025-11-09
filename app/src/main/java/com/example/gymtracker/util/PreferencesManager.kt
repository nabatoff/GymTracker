package com.example.gymtracker.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        val NOTIFICATION_TIME_KEY = stringPreferencesKey("notification_time")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }

    suspend fun setNotificationTime(time: String?) {
        context.dataStore.edit { preferences ->
            if (time != null) {
                preferences[NOTIFICATION_TIME_KEY] = time
            } else {
                preferences.remove(NOTIFICATION_TIME_KEY)
            }
        }
    }

    val notificationTime: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_TIME_KEY]
        }
}
