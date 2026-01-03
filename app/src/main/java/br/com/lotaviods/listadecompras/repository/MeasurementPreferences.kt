package br.com.lotaviods.listadecompras.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import br.com.lotaviods.listadecompras.manager.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object MeasurementPreferences {
    private val KEY_USE_IMPERIAL = booleanPreferencesKey("use_imperial")

    suspend fun setUseImperialSystem(context: Context, useImperial: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USE_IMPERIAL] = useImperial
        }
    }

    fun useImperialSystem(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_USE_IMPERIAL] ?: false
        }
    }
}
