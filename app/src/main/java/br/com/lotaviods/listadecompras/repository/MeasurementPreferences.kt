package br.com.lotaviods.listadecompras.repository

import android.content.Context
import androidx.core.content.edit

object MeasurementPreferences {
    private const val PREFS_NAME = "measurement_prefs"
    private const val KEY_USE_IMPERIAL = "use_imperial"

    fun setUseImperialSystem(context: Context, useImperial: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_USE_IMPERIAL, useImperial) }
    }

    fun useImperialSystem(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_USE_IMPERIAL, false)
    }
}