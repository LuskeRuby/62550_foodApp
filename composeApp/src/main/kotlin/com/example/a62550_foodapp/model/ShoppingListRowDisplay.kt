package com.example.a62550_foodapp.model

data class ShoppingListRowDisplay(
    val itemName: String,
    val quantity: Float,
    val unitType: String,
    val size: Float,
    val isChecked: Boolean,
    val category: String,
    val imagePath: String?
)
