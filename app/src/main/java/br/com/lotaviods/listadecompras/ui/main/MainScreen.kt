package br.com.lotaviods.listadecompras.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.ui.cart.CartItemRow
import br.com.lotaviods.listadecompras.ui.cart.EmptyCartScreen

enum class MainMenuAction {
    MANAGE_LISTS,
    LANGUAGE,
    THEME,
    MEASUREMENT,
    CURRENCY,
    SUPPORT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    items: List<Item>,
    listName: String,
    totalValue: String,
    onListNameChange: (String) -> Unit,
    onItemClick: (Item) -> Unit,
    onDeleteClick: (Item) -> Unit,
    onClearCartClick: () -> Unit,
    onAddItemClick: () -> Unit,
    onAddWidgetClick: () -> Unit,
    showAddWidgetButton: Boolean,
    onMenuAction: (MainMenuAction) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showClearCartDialog by remember { mutableStateOf(false) }

    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { showClearCartDialog = false },
            title = { Text(stringResource(R.string.clear_cart)) },
            text = { Text(stringResource(R.string.clear_cart_confirmation_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearCartClick()
                        showClearCartDialog = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCartDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.manage_lists)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.manage_lists)) },
                            onClick = {
                                showMenu = false
                                onMenuAction(MainMenuAction.MANAGE_LISTS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.language)) },
                            onClick = {
                                showMenu = false
                                onMenuAction(MainMenuAction.LANGUAGE)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.theme)) },
                            onClick = {
                                showMenu = false
                                onMenuAction(MainMenuAction.THEME)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.measurement_system)) },
                            onClick = {
                                showMenu = false
                                onMenuAction(MainMenuAction.MEASUREMENT)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.currency)) },
                            onClick = {
                                showMenu = false
                                onMenuAction(MainMenuAction.CURRENCY)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.support_me)) },
                            onClick = {
                                showMenu = false
                                onMenuAction(MainMenuAction.SUPPORT)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItemClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_new_item)
                )
            }
        }
    ) { paddingValues ->
        if (items.isEmpty()) {
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                EmptyCartScreen()
                if (showAddWidgetButton) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        ElevatedButton(
                            onClick = onAddWidgetClick,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(stringResource(R.string.add_widget))
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = listName,
                    onValueChange = onListNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    label = { Text(stringResource(R.string.list_name_hint)) },
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_pencil),
                            contentDescription = null
                        )
                    }
                )
                
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(items) { item ->
                        CartItemRow(
                            item = item,
                            onClick = { onItemClick(item) },
                            onDelete = { onDeleteClick(item) }
                        )
                    }
                    
                    item {
                        if (showAddWidgetButton) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ElevatedButton(
                                    onClick = onAddWidgetClick
                                ) {
                                    Text(stringResource(R.string.add_widget))
                                }
                            }
                        }
                    }
                }
                
                Text(
                    text = totalValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    fontSize = 20.sp
                )
                
                Button(
                    onClick = { showClearCartDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .padding(bottom = 30.dp)
                ) {
                    Text(stringResource(R.string.clear_cart))
                }
            }
        }
    }
}
