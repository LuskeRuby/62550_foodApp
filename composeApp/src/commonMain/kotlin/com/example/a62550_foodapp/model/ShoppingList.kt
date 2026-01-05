package com.example.a62550_foodapp.model

data class ShoppingList(
    val id: Int,
    val name: String,
    val items: List<ShoppingListItem> = emptyList()
)
