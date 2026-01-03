package br.com.lotaviods.listadecompras.manager

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

class LanguageManager(private val context: Context) {
    private val KEY_LANGUAGE = intPreferencesKey("selected_language_id")

    enum class LanguageType(val id: Byte) {
        PT(0x01),
        EN(0x02);

        companion object {
            private val lookup = entries.associateBy { it.id }

            fun fromId(id: Byte): LanguageType =
                lookup[id] ?: PT
        }

        fun toLocale(): Locale = when (this) {
            PT -> Locale("pt")
            EN -> Locale("en")
        }
    }

    suspend fun setLanguage(language: LanguageType) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = language.id.toInt()
        }

        applyLanguage(language)
    }

    fun getLanguage(): Flow<LanguageType> {
        return context.dataStore.data.map { preferences ->
            val id = preferences[KEY_LANGUAGE]?.toByte() ?: LanguageType.PT.id
            LanguageType.fromId(id)
        }
    }

    fun applyLanguage(language: LanguageType, context: Context? = null) {
        val locale = language.toLocale()
        Locale.setDefault(locale)

        val appConfig = Configuration(this.context.resources.configuration)
        appConfig.setLocale(locale)
        this.context.resources.updateConfiguration(appConfig, this.context.resources.displayMetrics)

        if (context != null && context !== this.context) {
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}