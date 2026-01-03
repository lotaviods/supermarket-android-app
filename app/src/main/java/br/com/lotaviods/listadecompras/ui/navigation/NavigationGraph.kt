package br.com.lotaviods.listadecompras.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.ui.cart.CartViewModel
import br.com.lotaviods.listadecompras.ui.form.FormScreen
import br.com.lotaviods.listadecompras.ui.form.FormViewModel
import br.com.lotaviods.listadecompras.ui.main.MainMenuAction
import br.com.lotaviods.listadecompras.ui.main.MainScreen
import org.koin.androidx.compose.koinViewModel

object Destinations {
    const val MAIN = "main"
    const val FORM = "form"
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onAddWidgetClick: () -> Unit,
    showAddWidgetButton: Boolean,
    onMenuAction: (MainMenuAction) -> Unit
) {
    NavHost(navController = navController, startDestination = Destinations.MAIN) {
        composable(Destinations.MAIN) {
            val viewModel: CartViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            MainScreen(
                items = uiState.items,
                listName = uiState.listName,
                totalValue = uiState.totalValue,
                onListNameChange = viewModel::updateListName,
                onItemClick = { item ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("item_to_edit", item)
                    navController.navigate(Destinations.FORM)
                },
                onDeleteClick = viewModel::deleteItem,
                onClearCartClick = viewModel::clearCart,
                onAddItemClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Item>("item_to_edit")
                    navController.navigate(Destinations.FORM)
                },
                onAddWidgetClick = onAddWidgetClick,
                showAddWidgetButton = showAddWidgetButton,
                onMenuAction = onMenuAction
            )
        }
        composable(Destinations.FORM) {
            val viewModel: FormViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            val itemToEdit = navController.previousBackStackEntry?.savedStateHandle?.get<Item>("item_to_edit")

            LaunchedEffect(itemToEdit) {
                if (itemToEdit != null) {
                    viewModel.initItem(itemToEdit)
                    navController.previousBackStackEntry?.savedStateHandle?.remove<Item>("item_to_edit")
                }
            }

            FormScreen(
                uiState = uiState,
                onNameChange = viewModel::onNameChange,
                onPriceChange = viewModel::onPriceChange,
                onQuantityChange = viewModel::onQuantityChange,
                onUnitChange = viewModel::onUnitChange,
                onSaveClick = {
                    viewModel.saveItem()
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
