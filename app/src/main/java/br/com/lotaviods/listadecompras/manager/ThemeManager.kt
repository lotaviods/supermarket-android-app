package br.com.lotaviods.listadecompras.manager

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "selected_theme"

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

    fun setTheme(context: Context, themeMode: ThemeMode) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putInt(KEY_THEME, themeMode.id.toInt())
            }

        applyTheme(themeMode)
    }

    fun getTheme(context: Context): ThemeMode {
        return runCatching {
            val id = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_THEME, ThemeMode.SYSTEM.id.toInt())
                .toByte()

            ThemeMode.fromId(id)
        }.getOrElse {
            ThemeMode.SYSTEM
        }
    }

    private fun applyTheme(themeMode: ThemeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode.toAppCompatDelegateMode())
    }

    fun applyTheme(context: Context) {
        val themeMode = getTheme(context)
        applyTheme(themeMode)
    }
}