package com.example.a62550_foodapp.db.projection

data class ItemWithPrice(
    val id: Int,
    val name: String,
    val size: Float,
    val unitType: String,
    val category: String,
    val imagePath: String?,
    val price: Float
)
