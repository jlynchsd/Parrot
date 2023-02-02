package com.fowlplay.parrot.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fowlplay.parrot.viewmodel.Settings
import kotlinx.coroutines.flow.map

class ParrotDataStore(private val context: Context) {

    val settingsFlow = context.dataStore.data.map {
        Settings.fromString(it[SETTINGS_KEY] ?: "")
    }

    suspend fun updateSettings(settings: Settings) =
        context.dataStore.edit {
            it[SETTINGS_KEY] = settings.toString()
        }

    suspend fun reset() {
        context.dataStore.edit {
            it.clear()
        }
    }

    private companion object {
        const val SETTINGS_TAG = "settings"
        val SETTINGS_KEY = stringPreferencesKey("settings_key")

        private val Context.dataStore by preferencesDataStore(
            name = SETTINGS_TAG
        )
    }
}
