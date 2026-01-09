package com.example.a62550_foodapp.db.projection

data class ShoppingListEntry(
    val itemId: Int,
    val itemName: String,
    val size: Float,
    val unitType: String,
    val quantity: Float,
    val isChecked: Boolean,
    val imagePath: String?,
    val category: String
)
