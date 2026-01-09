package com.example.a62550_foodapp.db.projection

data class ShoppingListSupermarketRow(
    val supermarketId: Int,
    val supermarketName: String,

    val itemGroupId: Int?,
    val itemId: Int,
    val itemName: String,
    val unit: String?,

    val quantity: Float,
    val isChecked: Boolean,

    val price: Float
)
