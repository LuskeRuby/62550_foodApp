package com.example.a62550_foodapp.db.projection

data class ShoppingListItemGroupEntry (
    val id: Long,           // shopping_list_item_group.id
    val name: String,
    val category: String,
    val unitType: String,
    val quantity: Int,
    val size: Float
)
