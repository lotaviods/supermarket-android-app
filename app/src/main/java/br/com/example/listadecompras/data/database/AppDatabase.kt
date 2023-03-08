package br.com.example.listadecompras.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.example.listadecompras.data.item.ItemDao
import br.com.example.listadecompras.model.item.Item

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDAO(): ItemDao
}