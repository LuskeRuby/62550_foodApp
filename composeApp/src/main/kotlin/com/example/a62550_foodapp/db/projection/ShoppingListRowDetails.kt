package com.example.a62550_foodapp.db.projection

data class ShoppingListRowDetails(
    val itemName: String,
    val quantity: Float,
    val unit: String?,
    val isChecked: Boolean,
    val itemGroupId: Int?,
    val cheapestPrice: Float?,
    val supermarketName: String?
)
