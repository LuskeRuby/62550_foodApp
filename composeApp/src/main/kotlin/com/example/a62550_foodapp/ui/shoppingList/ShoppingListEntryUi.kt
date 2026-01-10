package com.example.a62550_foodapp.ui.shoppingList

data class ShoppingListEntryUi(
    val itemId: Int,
    val name: String,
    val quantity: Float,
    val unitType: String,
    val isChecked: Boolean,
    val category: String,
    val size: Float,
    val price: Float?
)
