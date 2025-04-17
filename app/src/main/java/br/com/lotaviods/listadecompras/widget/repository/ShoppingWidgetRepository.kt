package br.com.lotaviods.listadecompras.widget.repository

import android.content.Context
import br.com.lotaviods.listadecompras.data.item.ItemDao
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.repository.CartRepository
import br.com.lotaviods.listadecompras.widget.model.WidgetState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ShoppingWidgetRepository(
    private val context: Context,
    private val dao: ItemDao,
) {
    private val preferences =
        context.getSharedPreferences(CartRepository.KEY_PREFERENCES, Context.MODE_PRIVATE)

    fun loadModel(): Flow<WidgetState> {
        return dao.getAllFlow().map {
            if (it.isNotEmpty())
                WidgetState.Loaded(
                    it,
                    listName()
                )
            else WidgetState.Empty
        }.catch { emit(WidgetState.Empty) }
         .distinctUntilChanged()
    }

    private fun listName(): String {
        return preferences.getString(CartRepository.KEY_NOME_LISTA, "") ?: ""
    }
}