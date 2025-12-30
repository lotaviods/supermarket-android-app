package br.com.lotaviods.listadecompras.data.list

import androidx.room.*
import br.com.lotaviods.listadecompras.model.list.ShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Insert
    suspend fun insert(list: ShoppingList): Long

    @Query("SELECT * FROM shopping_list ORDER BY created_at DESC")
    fun getAllLists(): Flow<List<ShoppingList>>

    @Query("SELECT * FROM shopping_list WHERE id = :id")
    suspend fun getListById(id: Int): ShoppingList?

    @Delete
    suspend fun delete(list: ShoppingList)

    @Update
    suspend fun update(list: ShoppingList)
}