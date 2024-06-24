package br.com.lotaviods.listadecompras.widget.model

import br.com.lotaviods.listadecompras.model.item.Item

sealed interface WidgetState {
    data object Empty : WidgetState
    data object Loading : WidgetState
    data class Loaded(val items: List<Item>, val listName: String): WidgetState
}
