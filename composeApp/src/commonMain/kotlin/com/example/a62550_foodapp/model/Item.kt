package com.example.a62550_foodapp.model

data class Item(
    val id: Int,
    val itemGroup: Int,
    val name: String,
    val unit: String?,
    val picture: ByteArray?,
    val weeklyPrices: List<ItemWeeklyPrice> = emptyList()
)
