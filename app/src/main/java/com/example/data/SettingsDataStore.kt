package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "device_inspector_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val DARK_MODE_KEY = stringPreferencesKey("dark_mode") // SYSTEM, DARK, LIGHT
        val DYNAMIC_THEME_KEY = booleanPreferencesKey("dynamic_theme")
        val TEMP_UNIT_KEY = stringPreferencesKey("temp_unit") // CELSIUS, FAHRENHEIT
        val FREQ_UNIT_KEY = stringPreferencesKey("freq_unit") // GHZ, MHZ
        val LANGUAGE_KEY = stringPreferencesKey("language") // "en", "es", "fr", "de"
    }

    val darkModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: "SYSTEM"
    }

    val dynamicThemeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DYNAMIC_THEME_KEY] ?: true
    }

    val tempUnitFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TEMP_UNIT_KEY] ?: "CELSIUS"
    }

    val freqUnitFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[FREQ_UNIT_KEY] ?: "GHZ"
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en"
    }

    suspend fun setDarkMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = mode
        }
    }

    suspend fun setDynamicTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_THEME_KEY] = enabled
        }
    }

    suspend fun setTempUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[TEMP_UNIT_KEY] = unit
        }
    }

    suspend fun setFreqUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[FREQ_UNIT_KEY] = unit
        }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = lang
        }
    }
}
