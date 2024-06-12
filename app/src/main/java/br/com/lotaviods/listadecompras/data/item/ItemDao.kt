package br.com.lotaviods.listadecompras.data.item

import androidx.room.*
import br.com.lotaviods.listadecompras.model.item.Item

@Dao
interface ItemDao {
    @Insert
    fun include(item: Item)

    @Query("SELECT * FROM item ORDER BY name")
    fun getAll(): MutableList<Item>

    @Query("SELECT * FROM item WHERE name LIKE :nome")
    fun getByName(nome: String): Item

    @Update
    fun update(item: Item)

    @Delete
    fun delete(item: Item)

    @Query("SELECT * FROM item WHERE category = :category")
    fun getItemByCategory(category: Int): MutableList<Item>

    @Query("DELETE FROM item")
    fun deletaTudo()
}