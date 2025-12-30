package br.com.lotaviods.listadecompras.data.item

import androidx.room.*
import br.com.lotaviods.listadecompras.model.item.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert
    fun include(item: Item)

    @Query("SELECT * FROM item ORDER BY name")
    fun getAll(): MutableList<Item>

    @Query("SELECT * FROM item ORDER BY name")
    fun getAllFlow(): Flow<MutableList<Item>>

    @Query("SELECT * FROM item WHERE name LIKE :nome")
    fun getByName(nome: String): Item

    @Update
    fun update(item: Item)

    @Delete
    fun delete(item: Item)

    @Query("SELECT * FROM item WHERE list_id = :listId ORDER BY name")
    fun getItemsByList(listId: Int): MutableList<Item>

    @Query("SELECT * FROM item WHERE list_id = :listId ORDER BY name")
    fun getItemsByListFlow(listId: Int): Flow<MutableList<Item>>

    @Query("SELECT * FROM item WHERE category = :category AND list_id = :listId")
    fun getItemByCategoryAndList(category: Int, listId: Int): MutableList<Item>

    @Query("DELETE FROM item WHERE list_id = :listId")
    fun deleteItemsByList(listId: Int)

    @Query("DELETE FROM item")
    fun deletaTudo()
}