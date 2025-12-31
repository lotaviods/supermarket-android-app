package br.com.lotaviods.listadecompras.manager

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import java.util.Locale

object LanguageManager {
    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language_id"

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

    fun setLanguage(context: Context, language: LanguageType) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putInt(KEY_LANGUAGE, language.id.toInt())
            }

        applyLanguage(context, language)
    }

    fun getLanguage(context: Context): LanguageType {
        return runCatching {
            val id = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_LANGUAGE, LanguageType.PT.id.toInt())
                .toByte()

            LanguageType.fromId(id)
        }.getOrElse {
            LanguageType.PT
        }
    }

    private fun applyLanguage(context: Context, language: LanguageType) {
        val locale = language.toLocale()
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun applyLanguage(context: Context) {
        val language = getLanguage(context)
        applyLanguage(context, language)
    }
}