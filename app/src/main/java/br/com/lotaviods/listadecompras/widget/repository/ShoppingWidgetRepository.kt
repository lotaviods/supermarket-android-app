package br.com.lotaviods.listadecompras.widget.repository

import android.content.Context
import br.com.lotaviods.listadecompras.data.item.ItemDao
import br.com.lotaviods.listadecompras.data.list.ShoppingListDao
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
    private val shoppingListDao: ShoppingListDao
) {
    private val preferences =
        context.getSharedPreferences(CartRepository.KEY_PREFERENCES, Context.MODE_PRIVATE)

    fun loadModel(): Flow<WidgetState> {
        val currentListId = preferences.getInt(CartRepository.KEY_CURRENT_LIST, 1)
        return dao.getItemsByListFlow(currentListId).map { items ->
            if (items.isNotEmpty()) {
                val listName = try {
                    shoppingListDao.getListById(currentListId)?.name ?: "Lista"
                } catch (e: Exception) {
                    "Lista"
                }
                WidgetState.Loaded(items, listName)
            } else WidgetState.Empty
        }.catch { emit(WidgetState.Empty) }
         .distinctUntilChanged()
    }
}