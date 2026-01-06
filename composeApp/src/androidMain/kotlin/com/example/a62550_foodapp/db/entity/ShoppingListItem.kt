package com.example.a62550_foodapp.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "shopping_list_items",
    primaryKeys = ["shopping_list_id", "item_id", "label"],
    foreignKeys = [
        ForeignKey(
            entity = ShoppingList::class,
            parentColumns = ["id"],
            childColumns = ["shopping_list_id"]
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"]
        )
    ]
)
data class ShoppingListItem(
    val shopping_list_id: Int,
    val item_id: Int,
    val quantity: Float,
    val is_checked: Boolean,
    val label: String
)