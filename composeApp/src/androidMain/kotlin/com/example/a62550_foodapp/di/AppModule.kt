@file:JvmName("AndroidAppModuleKt")
package com.example.a62550_foodapp.di

import androidx.room.Room
import com.example.a62550_foodapp.db.AppDatabase
import com.example.a62550_foodapp.viewmodel.AndroidMainViewModel
import com.example.a62550_foodapp.viewmodel.AndroidShoppingListDetailsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    // Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "food_app.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    // DAOs
    single { get<AppDatabase>().shoppingListDao() }
    single { get<AppDatabase>().itemDao() }
    single { get<AppDatabase>().recipeDao() }
    single { get<AppDatabase>().recipeItemDao() }
    single { get<AppDatabase>().shoppingListItemDao() }
    single { get<AppDatabase>().foodItemDao() }
    single { get<AppDatabase>().supermarketDao() }
    single { get<AppDatabase>().itemWeeklyPriceDao() }

    // ViewModels
    viewModel { AndroidMainViewModel(get()) }
    viewModel { AndroidShoppingListDetailsViewModel(get()) }
}
