package br.com.lotaviods.listadecompras.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import br.com.lotaviods.listadecompras.BuildConfig
import br.com.lotaviods.listadecompras.broadcast.PinWidgetReceiver
import br.com.lotaviods.listadecompras.manager.LanguageManager
import br.com.lotaviods.listadecompras.manager.ThemeManager
import br.com.lotaviods.listadecompras.ui.dialog.DialogHost
import br.com.lotaviods.listadecompras.ui.dialog.rememberDialogState
import br.com.lotaviods.listadecompras.ui.list.ListManagementViewModel
import br.com.lotaviods.listadecompras.ui.main.MainMenuAction
import br.com.lotaviods.listadecompras.ui.navigation.NavigationGraph
import br.com.lotaviods.listadecompras.ui.theme.ShoppingListTheme
import br.com.lotaviods.listadecompras.widget.ShoppingListWidgetReceiver
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val listManagementViewModel by viewModel<ListManagementViewModel>()
    private val themeManager by inject<ThemeManager>()
    private val languageManager by inject<LanguageManager>()
    
    private var currentLanguage by mutableStateOf(LanguageManager.LanguageType.PT)

    private val appWidgetManager: AppWidgetManager by lazy {
        getSystemService(AppWidgetManager::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            languageManager.getLanguage().collect { language ->
                currentLanguage = language
                languageManager.applyLanguage(language, this@MainActivity)
            }
        }
        
        themeManager.applyTheme()

        enableEdgeToEdge()

        setContent {
            val currentTheme by themeManager.getTheme().collectAsState(initial = ThemeManager.ThemeMode.SYSTEM)

            key(currentLanguage) {
                ShoppingListTheme {
                    val navController = rememberNavController()
                    val dialogState = rememberDialogState()

                    NavigationGraph(
                        navController = navController,
                        onAddWidgetClick = { requestPinAppWidget() },
                        showAddWidgetButton = checkWidgetSupport(),
                        onMenuAction = { action ->
                            when (action) {
                                MainMenuAction.MANAGE_LISTS -> dialogState.showManageLists()
                                MainMenuAction.LANGUAGE -> dialogState.showManageLang()
                                MainMenuAction.THEME -> dialogState.showManageTheme()
                                MainMenuAction.MEASUREMENT -> dialogState.showManageMeasurement()
                                MainMenuAction.CURRENCY -> dialogState.showManageCurrency()
                                MainMenuAction.SUPPORT -> openSupportLink()
                            }
                        }
                    )

                    DialogHost(
                        context = this@MainActivity,
                        state = dialogState,
                        listViewModel = listManagementViewModel,
                        currentTheme = currentTheme,
                        currentLanguage = currentLanguage,
                        onDismiss = { dialogState.close() }
                    )
                }
            }
        }
    }

    private fun checkWidgetSupport(): Boolean {
        val provider = ComponentName(this, ShoppingListWidgetReceiver::class.java)
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                appWidgetManager.isRequestPinAppWidgetSupported &&
                appWidgetManager.getAppWidgetIds(provider).isEmpty()
    }

    private fun requestPinAppWidget() {
        val provider = ComponentName(this, ShoppingListWidgetReceiver::class.java)

        val callback = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, PinWidgetReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            appWidgetManager.isRequestPinAppWidgetSupported
        ) {
            appWidgetManager.requestPinAppWidget(provider, null, callback)
        }
    }

    private fun openSupportLink() {
        startActivity(Intent(Intent.ACTION_VIEW, BuildConfig.SUPPORT_URL.toUri()))
    }
}
