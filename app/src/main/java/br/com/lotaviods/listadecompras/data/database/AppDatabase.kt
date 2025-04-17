package br.com.lotaviods.listadecompras.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.lotaviods.listadecompras.data.item.ItemDao
import br.com.lotaviods.listadecompras.model.item.Item

@Database(entities = [Item::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDAO(): ItemDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE item ADD COLUMN unidade TEXT")
            }
        }
    }
}