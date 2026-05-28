package com.lmu.setupmanager.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
private val USE_SYSTEM_THEME_KEY = booleanPreferencesKey("use_system_theme")

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[DARK_THEME_KEY] ?: false }

    val useSystemTheme: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[USE_SYSTEM_THEME_KEY] ?: true }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_THEME_KEY] = enabled
        }
    }

    suspend fun setUseSystemTheme(useSystem: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[USE_SYSTEM_THEME_KEY] = useSystem
        }
    }
}