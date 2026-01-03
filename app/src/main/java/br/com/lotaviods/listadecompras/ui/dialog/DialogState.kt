package br.com.lotaviods.listadecompras.ui.dialog

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun rememberDialogState(
    initial: DialogState.State = DialogState.State.None
): DialogState {
    return rememberSaveable(
        saver = DialogState.Saver
    ) {
        DialogState().apply {
            if (initial != DialogState.State.None) {
                value = initial
            }
        }
    }
}


@Stable
class DialogState {

    var value by mutableStateOf(State.None)

    enum class State {
        None,
        Theme,
        Language,
        Measurement,
        Currency,
        ListManagement
    }

    fun showManageLists() {
        value = State.ListManagement
    }

    fun showManageLang() {
        value = State.Language
    }

    fun showManageTheme() {
        value = State.Theme
    }

    fun showManageMeasurement() {
        value = State.Measurement
    }

    fun showManageCurrency() {
        value = State.Currency
    }

    fun close() {
        value = State.None
    }

    companion object {
        val Saver: Saver<DialogState, String> = Saver(
            save = { it.value.name },
            restore = { saved ->
                DialogState().apply {
                    value = State.valueOf(saved)
                }
            }
        )
    }
}
