package com.example.a62550_foodapp.model

// UI-facing data class
data class ShoppingListItem(
    val shopping_list_id: Int,
    val item_id: Int,
    val quantity: Int,
    val is_checked: Boolean,
    val label: String
)
