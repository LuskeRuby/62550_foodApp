package com.example.a62550_foodapp.di

import android.app.Application
import androidx.room.Room
import com.example.a62550_foodapp.db.AppDatabase
import com.example.a62550_foodapp.viewmodel.MainViewModel
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import com.example.a62550_foodapp.viewmodel.ShoppingListViewModel
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
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "food_app.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /* ---------- DAOs ---------- */

    single { get<AppDatabase>().itemGroupDao() }
    single { get<AppDatabase>().itemDao() }
    single { get<AppDatabase>().itemWeeklyPriceDao() }

    single { get<AppDatabase>().recipeDao() }
    single { get<AppDatabase>().recipeItemDao() }

    single { get<AppDatabase>().shoppingListDao() }
    single { get<AppDatabase>().shoppingListItemDao() }

    single { get<AppDatabase>().supermarketDao() }

    /* ---------- ViewModels ---------- */

    viewModel { MainViewModel(get()) }

    viewModel {
        RecipeViewModel(
            recipeDao = get(),
            recipeItemDao = get(),
            itemWeeklyPriceDao = get(),
            appContext = androidContext()
        )
    }

    viewModel{ ShoppingListViewModel(get()) }


    viewModel {
        ShoppingListDetailsViewModel(
            get(), // ShoppingListItemDao
            get()  // ItemDao
        )
    }
}
