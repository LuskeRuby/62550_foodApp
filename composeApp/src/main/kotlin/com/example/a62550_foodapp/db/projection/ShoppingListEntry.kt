package com.example.a62550_foodapp.db.projection

/**
 * Projection representing one shopping list entry resolved to a concrete store item.
 * Backed by a logical ingredient group (itemGroupId) which may come from a recipe.
 */
data class ShoppingListEntry(
    val itemId: Int,
    val itemGroupId: Int,
    val recipeId: Int?,

    val itemName: String,
    val size: Float,
    val unitType: String,
    val quantity: Int,
    val price: Float?,
    val isChecked: Boolean,
    val category: String
)