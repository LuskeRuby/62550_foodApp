package com.example.a62550_foodapp.db.projection

/**
 * Projection representing one shopping list entry resolved to a concrete store item.
 * Backed by a logical ingredient group (itemGroupId) which may come from a recipe.
 */
data class ShoppingListEntry(
    val id: Long, // ID from ShoppingListItemGroup

    val itemId: Long?,
    val itemGroupId: Long,
    val recipeId: Long?,
    val superMarketName: String?,
    val superMarketLogo: String?,

    val itemName: String,
    val category: String,

    val quantity: Int,
    val size: Float?,
    val unitType: String,

    val price: Float?,

    val isChecked: Boolean
)
