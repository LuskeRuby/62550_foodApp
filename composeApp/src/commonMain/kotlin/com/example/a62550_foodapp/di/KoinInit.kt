package com.example.a62550_foodapp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.core.module.Module

// Initialize Koin and allow platform modules to be passed in (e.g. androidModule)
fun initKoin(config: KoinAppDeclaration? = null, vararg platformModules: Module) {
    startKoin {
        config?.invoke(this)
        val allModules = listOf(appModule) + platformModules
        modules(allModules)
    }
}