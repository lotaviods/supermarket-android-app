package br.com.lotaviods.listadecompras.manager

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ThemeManager(private val context: Context) {
    enum class ThemeMode(val id: Byte) {
        SYSTEM(0x01),
        LIGHT(0x02),
        DARK(0x03);

        fun toAppCompatDelegateMode(): Int = when (this) {
            SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            DARK -> AppCompatDelegate.MODE_NIGHT_YES
        }

        companion object {
            private val lookup = entries.associateBy { it.id }

            fun fromId(id: Byte): ThemeMode =
                lookup[id] ?: SYSTEM
        }
    }

    suspend fun setTheme(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME] = themeMode.id.toInt()
        }

        applyTheme(themeMode)
    }

    fun getTheme(): Flow<ThemeMode> {
        return context.dataStore.data.map { preferences ->
            val id = preferences[KEY_THEME]?.toByte() ?: ThemeMode.SYSTEM.id
            ThemeMode.fromId(id)
        }
    }

    private fun applyTheme(themeMode: ThemeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode.toAppCompatDelegateMode())
    }

    fun applyTheme() {
        CoroutineScope(Dispatchers.Main).launch {
            val themeMode = getTheme().first()
            applyTheme(themeMode)
        }
    }

    companion object {
        private val KEY_THEME = intPreferencesKey("selected_theme")
    }
}