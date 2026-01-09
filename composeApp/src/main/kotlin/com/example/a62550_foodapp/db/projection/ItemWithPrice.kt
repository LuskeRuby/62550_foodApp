package com.example.a62550_foodapp.db.projection

data class ItemWithPrice(
    val itemId: Int,
    val itemName: String,
    val category: String,
    val price: Float
)
