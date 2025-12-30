package br.com.lotaviods.listadecompras.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.lotaviods.listadecompras.constantes.Constants
import br.com.lotaviods.listadecompras.data.item.ItemDao
import br.com.lotaviods.listadecompras.data.list.ShoppingListDao
import br.com.lotaviods.listadecompras.model.item.Item
import br.com.lotaviods.listadecompras.model.list.ShoppingList

@Database(entities = [Item::class, ShoppingList::class], version = 4)
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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new column unit_int
                database.execSQL("ALTER TABLE item ADD COLUMN unit_int INTEGER NOT NULL DEFAULT ${Constants.UNIT_PIECE}")
                
                // Migrate data from old 'unidade' (TEXT) to 'unit_int' (INTEGER)
                val cursor = database.query("SELECT uid, unidade FROM item")
                while (cursor.moveToNext()) {
                    val uid = cursor.getInt(0)
                    val unidadeStr = cursor.getString(1)
                    val unitInt = when (unidadeStr?.lowercase()) {
                        "gramas", "grams", "g" -> Constants.UNIT_GRAMS
                        "kg", "kilogramas", "k" -> Constants.UNIT_KG
                        "litros", "liters", "l" -> Constants.UNIT_LITERS
                        "ml", "mililitros" -> Constants.UNIT_ML
                        "unidade", "unit", "piece" -> Constants.UNIT_PIECE
                        "nenhum", "none" -> Constants.UNIT_NONE
                        else -> Constants.UNIT_PIECE
                    }
                    database.execSQL("UPDATE item SET unit_int = $unitInt WHERE uid = $uid")
                }
                cursor.close()

                // Since SQLite doesn't support DROP COLUMN easily in older versions or some implementations,
                // and Room usually recommends recreating the table or just ignoring the old column.
                // However, to keep it clean, we usually create a temp table.
                // But for simplicity and safety, we can just keep the old column or if we strictly follow Room migration:
                // We will create a new table, copy data, drop old table, rename new table.
                
                database.execSQL("CREATE TABLE item_new (uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT, value TEXT, category INTEGER, qnt INTEGER, unit_int INTEGER NOT NULL DEFAULT ${Constants.UNIT_PIECE}, list_id INTEGER NOT NULL DEFAULT 1)")
                
                database.execSQL("INSERT INTO item_new (uid, name, value, category, qnt, unit_int, list_id) SELECT uid, name, value, category, qnt, unit_int, list_id FROM item")
                
                database.execSQL("DROP TABLE item")
                database.execSQL("ALTER TABLE item_new RENAME TO item")
            }
        }
    }
}
