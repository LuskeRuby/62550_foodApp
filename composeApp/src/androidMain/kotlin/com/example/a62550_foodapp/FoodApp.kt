package com.example.a62550_foodapp

import android.app.Application
import com.example.a62550_foodapp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class FoodApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@FoodApp)
        }
    }
}