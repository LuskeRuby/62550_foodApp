package com.example.a62550_foodapp.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.a62550_foodapp.BuildConfig
import com.example.a62550_foodapp.db.AppDatabase
import com.example.a62550_foodapp.db.DatabaseSeeder
import com.example.a62550_foodapp.viewmodel.MainViewModel
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Call this once from Application.onCreate()
 */
fun startKoinApp(app: Application) {
    startKoin {
        androidContext(app)
        modules(appModule)
    }
}


val appModule = module {

    /* ---------- Database ---------- */

    single {
        lateinit var database: AppDatabase

        database = Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "food_app.db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    if (BuildConfig.DEBUG) {
                        CoroutineScope(Dispatchers.IO).launch {
                            DatabaseSeeder.seed(database)
                        }
                    }
                }
            })
            .build()

        database
    }

    /* ---------- DAOs ---------- */

    single { get<AppDatabase>().shoppingListDao() }
    single { get<AppDatabase>().itemDao() }
    single { get<AppDatabase>().recipeDao() }
    single { get<AppDatabase>().recipeItemDao() }
    single { get<AppDatabase>().shoppingListItemDao() }
    single { get<AppDatabase>().foodItemDao() }
    single { get<AppDatabase>().supermarketDao() }
    single { get<AppDatabase>().itemWeeklyPriceDao() }

    /* ---------- ViewModels ---------- */

    viewModel { MainViewModel(get()) }
    viewModel {
        RecipeViewModel(
            recipeDao = get(),
            appContext = androidContext()
        )
    }
    viewModel { ShoppingListDetailsViewModel(get()) }
}
