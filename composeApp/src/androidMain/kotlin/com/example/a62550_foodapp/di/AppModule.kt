@file:JvmName("AndroidAppModuleKt")
package com.example.a62550_foodapp.di

import androidx.room.Room
import com.example.a62550_foodapp.db.AppDatabase
import com.example.a62550_foodapp.viewmodel.AndroidMainViewModel
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
        ).build()
    }

    // DAOs
    single { get<AppDatabase>().foodItemDao() }

    // ViewModels
    viewModel { AndroidMainViewModel(get()) }
}
