package br.com.lotaviods.listadecompras.ui.app

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.lotaviods.listadecompras.R
import br.com.lotaviods.listadecompras.data.database.AppDatabase
import br.com.lotaviods.listadecompras.manager.LanguageManager
import br.com.lotaviods.listadecompras.repository.CartRepository
import br.com.lotaviods.listadecompras.repository.ItemRepository
import br.com.lotaviods.listadecompras.widget.repository.ShoppingWidgetRepository
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

class Application : android.app.Application() {
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-database"
        ).addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4)
                .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val defaultName = applicationContext.getString(R.string.default_list_name)
                db.execSQL("INSERT INTO shopping_list (id, name, created_at) VALUES (1, '$defaultName', ${System.currentTimeMillis()})")
            }
        }).build()
    }

    private val appModule = module {
        single {
            CartRepository(applicationContext, database.shoppingListDAO(), database.itemDAO())
        }
        single {
            ItemRepository(database.itemDAO(), get())
        }
        single {
            ShoppingWidgetRepository(applicationContext, database.itemDAO(), database.shoppingListDAO())
        }
    }

    override fun onCreate() {
        super.onCreate()
        LanguageManager.applyLanguage(this)
        
        if (GlobalContext.getOrNull() == null) {
            GlobalContext.startKoin {
                androidLogger()
                androidContext(this@Application)
                modules(appModule)
            }
        }
    }
}