package br.com.lotaviods.listadecompras.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.manager.ThemeManager

@Composable
fun ThemeSelectionDialog(
    currentTheme: ThemeManager.ThemeMode,
    onThemeSelected: (ThemeManager.ThemeMode) -> Unit,
    onDismissRequest: () -> Unit
) {
    val options = listOf(
        ThemeManager.ThemeMode.SYSTEM to stringResource(R.string.theme_system),
        ThemeManager.ThemeMode.LIGHT to stringResource(R.string.theme_light),
        ThemeManager.ThemeMode.DARK to stringResource(R.string.theme_dark)
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.select_theme)) },
        text = {
            Column(Modifier.selectableGroup()) {
                options.forEach { (mode, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (mode == currentTheme),
                                onClick = { onThemeSelected(mode) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (mode == currentTheme),
                            onClick = null
                        )
                        Text(
                            text = label,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
