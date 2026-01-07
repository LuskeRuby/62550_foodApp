package com.example.a62550_foodapp

import android.app.Application
import com.example.a62550_foodapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FoodApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@FoodApp)
            modules(appModule)
        }
    }
}
