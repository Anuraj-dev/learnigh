package com.anuraj.learnigh.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "learnigh_settings")

class SettingsDataStore(private val context: Context) {
    private object Keys {
        val displayName = stringPreferencesKey("display_name")
        val streakDays = intPreferencesKey("streak_days")
        val lastActiveDay = stringPreferencesKey("last_active_day")
        val remindersDefault = booleanPreferencesKey("reminders_default")
    }

    val displayName: Flow<String> = context.dataStore.data.map {
        it[Keys.displayName] ?: "Raja"
    }

    val streakDays: Flow<Int> = context.dataStore.data.map {
        it[Keys.streakDays] ?: 3
    }

    val remindersDefault: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.remindersDefault] ?: true
    }

    suspend fun setDisplayName(name: String) {
        context.dataStore.edit { it[Keys.displayName] = name }
    }

    suspend fun setRemindersDefault(enabled: Boolean) {
        context.dataStore.edit { it[Keys.remindersDefault] = enabled }
    }

    suspend fun bumpStreak(todayKey: String) {
        context.dataStore.edit { prefs ->
            val last = prefs[Keys.lastActiveDay]
            val current = prefs[Keys.streakDays] ?: 0
            when {
                last == todayKey -> Unit
                last == null -> {
                    prefs[Keys.streakDays] = 1
                    prefs[Keys.lastActiveDay] = todayKey
                }
                else -> {
                    prefs[Keys.streakDays] = current + 1
                    prefs[Keys.lastActiveDay] = todayKey
                }
            }
        }
    }
}
