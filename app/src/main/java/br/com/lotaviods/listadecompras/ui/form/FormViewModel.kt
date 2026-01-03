package br.com.lotaviods.listadecompras.ui.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FormViewModel(
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState

    fun initItem(itemToEdit: Item) {
        _uiState.value = FormUiState(
            uid = itemToEdit.uid,
            name = itemToEdit.name,
            price = itemToEdit.value,
            quantity = itemToEdit.quantity,
            unit = itemToEdit.unit,
            category = itemToEdit.category,
            listId = itemToEdit.listId
        )
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onPriceChange(value: String) {
        _uiState.update { it.copy(price = value) }
    }

    fun onQuantityChange(value: Int) {
        _uiState.update { it.copy(quantity = value.coerceIn(0, 9999)) }
    }

    fun onUnitChange(value: Int) {
        _uiState.update { it.copy(unit = value) }
    }

    fun saveItem() {
        val state = _uiState.value

        val item = Item(
            uid = state.uid,
            name = state.name,
            value = state.price,
            quantity = state.quantity,
            unit = state.unit,
            category = state.category,
            listId = state.listId
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (state.uid != 0) {
                itemRepository.editItem(item)
            } else {
                itemRepository.insertItem(item)
            }
        }
    }
}
