package com.example.a62550_foodapp.db.projection

import com.example.a62550_foodapp.db.entity.ShoppingListItemGroup


/**
 * Projection representing one shopping list entry resolved to a concrete store item.
 * Backed by a logical ingredient group (itemGroupId) which may come from a recipe.
 */
data class ShoppingListEntry(
    val id: Long, // ID of ShoppingListItemGroup

    val itemId: Long?,
    val itemGroupId: Long,
    val recipeId: Long?,
    val superMarketName: String?,

    val itemName: String,
    val category: String,

    val quantity: Int,
    val size: Float?,
    val unitType: String,

    val price: Float?,

    val isChecked: Boolean
) {
    fun toShoppingListItemGroup(shoppingListId: Long): ShoppingListItemGroup {
        return ShoppingListItemGroup(
            id = id,
            shoppingListId = shoppingListId,
            itemGroupId = itemGroupId,
            recipeId = recipeId,
            portionQuantity = quantity,
            portionSize = size ?: 0f,
            isChecked = isChecked
        )
    }
}
