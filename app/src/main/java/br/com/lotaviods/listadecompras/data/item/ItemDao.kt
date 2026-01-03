package br.com.lotaviods.listadecompras.data.item

import androidx.room.*
import br.com.lotaviods.listadecompras.model.item.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Insert
    suspend fun include(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM item WHERE uid = :uid")
    suspend fun getItemById(uid: Int): Item?

    @Query("SELECT * FROM item WHERE list_id = :listId ORDER BY name")
    suspend fun getItemsByList(listId: Int): List<Item>

    @Query("SELECT * FROM item WHERE list_id = :listId ORDER BY name")
    fun getItemsByListFlow(listId: Int): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE category = :category AND list_id = :listId")
    suspend fun getItemByCategoryAndList(
        category: Int,
        listId: Int
    ): List<Item>

    @Query("DELETE FROM item WHERE list_id = :listId")
    suspend fun deleteItemsByList(listId: Int)
}
