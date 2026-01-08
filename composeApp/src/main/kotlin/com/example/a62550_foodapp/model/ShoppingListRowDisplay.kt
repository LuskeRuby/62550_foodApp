package com.example.a62550_foodapp.model

data class ShoppingListRowDisplay(
    val itemName: String,
    val quantity: Float,
    val unit: String?,
    val isChecked: Boolean,
    val supermarketName: String?,
    val cheapestPrice: Float?
)
