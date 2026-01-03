package br.com.lotaviods.listadecompras.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lotaviods.listadecompras.model.list.ShoppingList
import br.com.lotaviods.listadecompras.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListManagementViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListManagementUiState())
    val uiState: StateFlow<ListManagementUiState> = _uiState.asStateFlow()

    init {
        loadLists()
        loadCurrentListId()
    }

    private fun loadLists() {
        viewModelScope.launch {
            cartRepository.getAllLists().collect { lists ->
                _uiState.update { it.copy(lists = lists) }
            }
        }
    }

    private fun loadCurrentListId() {
        _uiState.update { it.copy(currentListId = cartRepository.getCurrentListId()) }
    }

    fun setCurrentList(list: ShoppingList) {
        cartRepository.setCurrentListId(list.id)
        _uiState.update { it.copy(currentListId = list.id) }
    }

    fun createList(name: String) {
        viewModelScope.launch {
            cartRepository.createList(name)
        }
    }

    fun deleteList(list: ShoppingList) {
        viewModelScope.launch {
            cartRepository.deleteList(list)
            if (cartRepository.getCurrentListId() == list.id) {
                cartRepository.setCurrentListId(1)
                _uiState.update { it.copy(currentListId = 1) }
            }
        }
    }
}

data class ListManagementUiState(
    val lists: List<ShoppingList> = emptyList(),
    val currentListId: Int = 1
)
