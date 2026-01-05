package com.example.a62550_foodapp.model

data class ShoppingListItem(
    val shoppingListId: Int,
    val itemId: Int,
    val quantity: Double,
    val isChecked: Boolean,
    val label: String
)
