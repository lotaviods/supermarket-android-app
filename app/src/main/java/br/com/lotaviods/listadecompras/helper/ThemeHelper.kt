package br.com.lotaviods.listadecompras.helper

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

object ThemeHelper {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "selected_theme"

    enum class ThemeMode(val value: Int) {
        SYSTEM(0),
        LIGHT(1),
        DARK(2);

        fun toAppCompatDelegateMode(): Int = when (this) {
            SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            DARK -> AppCompatDelegate.MODE_NIGHT_YES
        }
    }

    fun setTheme(context: Context, themeMode: ThemeMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putInt(KEY_THEME, themeMode.value) }
        applyTheme(themeMode)
    }

    fun getTheme(context: Context): ThemeMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val value = prefs.getInt(KEY_THEME, ThemeMode.SYSTEM.value)
        return ThemeMode.entries.find { it.value == value } ?: ThemeMode.SYSTEM
    }

    private fun applyTheme(themeMode: ThemeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode.toAppCompatDelegateMode())
    }

    fun applyTheme(context: Context) {
        val themeMode = getTheme(context)
        applyTheme(themeMode)
    }
}
