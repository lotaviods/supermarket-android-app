package br.com.lotaviods.listadecompras.ui.dialog

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.manager.CurrencyManager
import br.com.lotaviods.listadecompras.manager.LanguageManager
import br.com.lotaviods.listadecompras.manager.ThemeManager
import br.com.lotaviods.listadecompras.repository.MeasurementPreferences
import br.com.lotaviods.listadecompras.ui.common.SingleChoiceDialog
import br.com.lotaviods.listadecompras.ui.list.ListManagementDialog
import br.com.lotaviods.listadecompras.ui.list.ListManagementViewModel
import br.com.lotaviods.listadecompras.ui.theme.ThemeSelectionDialog
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DialogHost(
    context: Context,
    state: DialogState,
    listViewModel: ListManagementViewModel,
    currentTheme: ThemeManager.ThemeMode,
    currentLanguage: LanguageManager.LanguageType,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
    // Inject managers using Koin
    val themeManager = koinInject<ThemeManager>()
    val languageManager = koinInject<LanguageManager>()
    val currencyManager = koinInject<CurrencyManager>()

    when (state.value) {
        DialogState.State.None -> Unit

        DialogState.State.Theme -> {
            ThemeSelectionDialog(
                currentTheme = currentTheme,
                onThemeSelected = {
                    scope.launch {
                        themeManager.setTheme(it)
                        onDismiss()
                    }
                },
                onDismissRequest = onDismiss
            )
        }

        DialogState.State.ListManagement -> {
            val uiState by listViewModel.uiState.collectAsState()
            ListManagementDialog(
                lists = uiState.lists,
                currentListId = uiState.currentListId,
                onDismissRequest = onDismiss,
                onListClick = {
                    listViewModel.setCurrentList(it)
                    onDismiss()
                },
                onDeleteListClick = listViewModel::deleteList,
                onCreateListClick = listViewModel::createList
            )
        }

        DialogState.State.Language -> {
            SingleChoiceDialog(
                title = stringResource(R.string.select_language),
                options = listOf(
                    LanguageManager.LanguageType.PT to stringResource(R.string.portuguese),
                    LanguageManager.LanguageType.EN to stringResource(R.string.english)
                ),
                selectedOption = currentLanguage,
                onOptionSelected = { language ->
                    scope.launch {
                        languageManager.setLanguage(language)
                        onDismiss()
                    }
                },
                onDismissRequest = onDismiss
            )
        }

        DialogState.State.Measurement -> {
            val useImperial by MeasurementPreferences.useImperialSystem(context).collectAsState(initial = false)
            SingleChoiceDialog(
                title = stringResource(R.string.select_measurement_system),
                options = listOf(
                    false to stringResource(R.string.system_metric),
                    true to stringResource(R.string.system_imperial)
                ),
                selectedOption = useImperial,
                onOptionSelected = { newValue ->
                    scope.launch {
                        MeasurementPreferences.setUseImperialSystem(context, newValue)
                        onDismiss()
                    }
                },
                onDismissRequest = onDismiss
            )
        }

        DialogState.State.Currency -> {
            val currentCurrency by currencyManager.getCurrency().collectAsState(initial = CurrencyManager.CurrencyType.BRL)
            SingleChoiceDialog(
                title = stringResource(R.string.select_currency),
                options = listOf(
                    CurrencyManager.CurrencyType.BRL to stringResource(R.string.currency_brl),
                    CurrencyManager.CurrencyType.USD to stringResource(R.string.currency_usd),
                    CurrencyManager.CurrencyType.EUR to stringResource(R.string.currency_eur)
                ),
                selectedOption = currentCurrency,
                onOptionSelected = {
                    scope.launch {
                        currencyManager.setCurrency(it)
                        onDismiss()
                    }
                },
                onDismissRequest = onDismiss
            )
        }
    }
}
