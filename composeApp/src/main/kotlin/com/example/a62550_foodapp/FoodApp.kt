package com.example.a62550_foodapp

import android.app.Application
import com.example.a62550_foodapp.di.startKoinApp

class FoodApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoinApp(this@FoodApp)
    }
}
