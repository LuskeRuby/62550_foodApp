package com.example.a62550_foodapp.di

import org.koin.dsl.module

// Define your shared dependencies here
val appModule = module {
    // Example: generic repository or viewmodel
    // factory { MyViewModel(get()) }
    // single { MyRepository() }
    single { "Success! Koin is working on Android." }
}
