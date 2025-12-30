package br.com.lotaviods.listadecompras.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.lotaviods.listadecompras.data.item.ItemDao
import br.com.lotaviods.listadecompras.data.list.ShoppingListDao
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.model.list.ShoppingList

@Database(entities = [Item::class, ShoppingList::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDAO(): ItemDao
    abstract fun shoppingListDAO(): ShoppingListDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE item ADD COLUMN unidade TEXT")
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE shopping_list (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, created_at INTEGER NOT NULL)")
                database.execSQL("ALTER TABLE item ADD COLUMN list_id INTEGER NOT NULL DEFAULT 1")
                database.execSQL("INSERT INTO shopping_list (id, name, created_at) VALUES (1, 'Lista Principal', ${System.currentTimeMillis()})")
            }
        }
    }
}