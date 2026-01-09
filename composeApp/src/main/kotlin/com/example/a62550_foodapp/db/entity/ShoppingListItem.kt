package com.example.a62550_foodapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "shopping_list_items",
    primaryKeys = ["shopping_list_id", "item_id"],
    foreignKeys = [
        ForeignKey(
            entity = ShoppingList::class,
            parentColumns = ["id"],
            childColumns = ["shopping_list_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("shopping_list_id"),
        Index("item_id")
    ]
)
data class ShoppingListItem(
    @ColumnInfo(name = "shopping_list_id")
    val shoppingListId: Int,

    @ColumnInfo(name = "item_id")
    val itemId: Int,

    @ColumnInfo(name = "calc_quantity")
    val calcQuantity: Float,

    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean
)
