package com.example.a62550_foodapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform