package br.com.lotaviods.listadecompras.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.helper.PriceHelper
import br.com.lotaviods.listadecompras.manager.CurrencyManager
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.repository.CartRepository
import br.com.lotaviods.listadecompras.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    application: Application,
    private val itemRepository: ItemRepository,
    private val cartRepository: CartRepository,
    private val currencyManager: CurrencyManager
) : AndroidViewModel(application) {

    private val itemsFlow =
        itemRepository.getItemsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val listNameFlow =
        cartRepository.currentListId
            .mapLatest { listId ->
                cartRepository.shoppingListDao
                    .getListById(listId)
                    ?.name
                    ?: getApplication<Application>()
                        .getString(R.string.default_list_name)
            }

    private val currencyFlow =
        currencyManager.getCurrency()

    private val totalValueFlow =
        combine(itemsFlow, currencyFlow) { items, currency ->
            val total = items.sumOf {
                PriceHelper.calculateTotalValue(
                    it.quantity ?: 0,
                    it.value,
                    it.unit
                )
            }

            val formatted = PriceHelper.formatPrice(
                total.toString(),
                currency
            )

            getApplication<Application>().getString(
                R.string.total_value,
                formatted
            )
        }

    val uiState: StateFlow<CartUiState> =
        combine(
            itemsFlow,
            listNameFlow,
            totalValueFlow
        ) { items, listName, totalValue ->
            CartUiState(
                items = items,
                listName = listName,
                totalValue = totalValue
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CartUiState()
        )

    private var updateListJob: Job? = null

    fun updateListName(name: String) {
        updateListJob?.cancel()

        updateListJob = viewModelScope.launch(Dispatchers.IO) {
            delay(1_000)
            val listId = cartRepository.getCurrentListId()
            cartRepository.shoppingListDao
                .getListById(listId)
                ?.let {
                    cartRepository.shoppingListDao.update(
                        it.copy(name = name)
                    )
                }
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.deleteItem(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.deleteAllItems()
        }
    }
}

data class CartUiState(
    val items: List<Item> = emptyList(),
    val listName: String = "",
    val totalValue: String = ""
)
