package br.com.lotaviods.listadecompras.repository

import android.content.Context
import androidx.core.content.edit
import br.com.lotaviods.listadecompras.data.item.ItemDao
import br.com.lotaviods.listadecompras.data.list.ShoppingListDao
import br.com.lotaviods.listadecompras.model.list.ShoppingList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartRepository(context: Context, val shoppingListDao: ShoppingListDao, private val itemDao: ItemDao) {
    private val preferences = context.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)

    private val _currentListId = MutableStateFlow(preferences.getInt(KEY_CURRENT_LIST, 1))
    val currentListId: StateFlow<Int> = _currentListId.asStateFlow()

    fun setCurrentListId(listId: Int) {
        preferences.edit {
            putInt(KEY_CURRENT_LIST, listId)
        }
        _currentListId.value = listId
    }
    
    fun getCurrentListId(): Int {
        return preferences.getInt(KEY_CURRENT_LIST, 1)
    }
    
    suspend fun createList(name: String): Long {
        return shoppingListDao.insert(ShoppingList(name = name))
    }
    
    fun getAllLists(): Flow<List<ShoppingList>> {
        return shoppingListDao.getAllLists()
    }
    
    suspend fun deleteList(list: ShoppingList) {
        itemDao.deleteItemsByList(list.id)
        shoppingListDao.delete(list)
    }

    companion object {
        const val KEY_PREFERENCES = "cart"
        const val KEY_CURRENT_LIST = "current_list"
    }
}
