package com.example.a62550_foodapp.model

// UI-facing data class
data class Item(
    val id: Int,
    val itemgroup: Int?,
    val name: String,
    val unit: String?,
    val picture: ByteArray?,
    val weeklyPrices: List<ItemWeeklyPrice> = emptyList()
)
