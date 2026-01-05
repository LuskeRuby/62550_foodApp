package com.example.a62550_foodapp.di

import org.koin.dsl.module

// Common/shared module for all platforms. Platform modules should use distinct names (e.g. androidModule) and be added in platform init.
val appModule = module {
    // Example: generic repository or viewmodel
    // factory { MyViewModel(get()) }
    // single { MyRepository() }
    single { "Success! Koin is working on multiple platforms." }
}
