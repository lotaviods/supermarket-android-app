package br.com.lotaviods.listadecompras.repository

import br.com.lotaviods.listadecompras.data.item.ItemDao
import br.com.lotaviods.listadecompras.model.item.Item

class ItemRepository(private val dao: ItemDao, private val cartRepository: CartRepository) {

    suspend fun getAllItems(): List<Item> {
        return dao.getItemsByList(cartRepository.getCurrentListId())
    }

    suspend fun insertItem(item: Item) {
        dao.include(item.copy(listId = cartRepository.getCurrentListId()))
    }

    suspend fun deleteItem(item: Item) {
        dao.delete(item)
    }

    suspend fun deleteAllItems() {
        dao.deleteItemsByList(cartRepository.getCurrentListId())
    }

    suspend fun getItemsByCategory(category: Int): List<Item> {
        return dao.getItemByCategoryAndList(category, cartRepository.getCurrentListId())
    }

    suspend fun editItem(item: Item) {
        return dao.update(item)
    }
}