package br.com.example.listadecompras.repository

import br.com.example.listadecompras.data.item.ItemDao
import br.com.example.listadecompras.model.item.Item

class ItemRepository(private val dao: ItemDao) {

    suspend fun getAllItems(): MutableList<Item> {
        return dao.getAll()
    }

    suspend fun insertItem(item: Item) {
        dao.include(item)
    }

    suspend fun deleteItem(item: Item) {
        dao.delete(item)
    }

    suspend fun deletaTodosOsItens() {
        dao.deletaTudo()
    }

    suspend fun getItensPelaCategoria(category: Int): MutableList<Item> {
        return dao.getItemByCategory(category)
    }

    suspend fun editaItem(item: Item) {
        return dao.update(item)
    }
}