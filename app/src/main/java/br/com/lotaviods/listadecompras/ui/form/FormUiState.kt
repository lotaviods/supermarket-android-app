package br.com.lotaviods.listadecompras.ui.form

import br.com.lotaviods.listadecompras.constantes.Constants

data class FormUiState(
    val uid: Int = 0,
    val name: String? = "",
    val price: String? = "",
    val quantity: Int? = 1,
    val unit: Int = Constants.UNIT_PIECE,
    val category: Int? = 0,
    val listId: Int = 1
)
