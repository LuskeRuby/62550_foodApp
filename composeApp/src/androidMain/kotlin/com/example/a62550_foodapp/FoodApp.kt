package com.example.a62550_foodapp

import android.app.Application
import com.example.a62550_foodapp.di.androidModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import com.example.a62550_foodapp.db.DatabaseInitializer
import org.koin.android.ext.android.inject

class FoodApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@FoodApp)
            modules(androidModule)
        }

        // rigger the Database Initializer, using koin
        val initializer: DatabaseInitializer by inject()
        initializer.init()
    }
}
