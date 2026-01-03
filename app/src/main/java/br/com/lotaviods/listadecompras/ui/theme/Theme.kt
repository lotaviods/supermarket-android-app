package br.com.lotaviods.listadecompras.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import br.com.lotaviods.listadecompras.manager.ThemeManager
import org.koin.compose.koinInject

private val DarkColorScheme = darkColorScheme(
)

private val LightColorScheme = lightColorScheme(
)

@Composable
fun ShoppingListTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val themeManager = koinInject<ThemeManager>()
    val themeMode by themeManager.getTheme().collectAsState(initial = ThemeManager.ThemeMode.SYSTEM)
    
    val isDark = when (themeMode) {
        ThemeManager.ThemeMode.DARK -> true
        ThemeManager.ThemeMode.LIGHT -> false
        ThemeManager.ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
