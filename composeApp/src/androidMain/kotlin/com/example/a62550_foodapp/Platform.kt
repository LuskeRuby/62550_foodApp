package com.example.a62550_foodapp

import android.os.Build

interface Platform {
    val name: String
}

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

fun getPlatform(): Platform = AndroidPlatform()
