@file:JvmName("AndroidAppModuleKt")
package com.example.a62550_foodapp.di

import androidx.room.Room
import com.example.a62550_foodapp.db.AppDatabase
import com.example.a62550_foodapp.db.DatabaseInitializer // Make sure to import this
import com.example.a62550_foodapp.viewmodel.AndroidMainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    // 1. Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "food_app.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // 2. Database Initializer
    single { DatabaseInitializer(get()) }

    // DAOs
    single { get<AppDatabase>().shoppingListDao() }
    single { get<AppDatabase>().itemDao() }
    single { get<AppDatabase>().recipeDao() }
    single { get<AppDatabase>().recipeItemDao() }
    single { get<AppDatabase>().shoppingListItemDao() }
    single { get<AppDatabase>().foodItemDao() }
    single { get<AppDatabase>().supermarketDao() }

    // ViewModels
    viewModel { AndroidMainViewModel(get()) }
}
