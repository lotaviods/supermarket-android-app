package br.com.lotaviods.listadecompras.ui.app

import androidx.room.Room
import br.com.lotaviods.listadecompras.data.database.AppDatabase
import br.com.lotaviods.listadecompras.repository.CartRepository
import br.com.lotaviods.listadecompras.repository.ItemRepository
import br.com.lotaviods.listadecompras.widget.repository.ShoppingWidgetRepository
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class Application : android.app.Application() {
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app-database"
        ).build()
    }

    private val appModule = module {
        single {
            ItemRepository(database.itemDAO())
        }
        single {
            CartRepository(applicationContext)
        }
        single {
            ShoppingWidgetRepository(applicationContext, database.itemDAO())
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(appModule)
        }


    }


}