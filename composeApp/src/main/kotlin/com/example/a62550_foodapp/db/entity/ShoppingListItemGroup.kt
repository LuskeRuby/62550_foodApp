package com.example.a62550_foodapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_list_item_groups",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingList::class,
            parentColumns = ["id"],
            childColumns = ["shopping_list_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemGroup::class,
            parentColumns = ["id"],
            childColumns = ["item_group_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        // Enforce uniqueness when recipe_id IS NOT NULL
        Index(
            value = ["shopping_list_id", "item_group_id", "recipe_id"],
            unique = true
        ),
        // Enforce uniqueness when recipe_id IS NULL
        Index(
            value = ["shopping_list_id", "item_group_id"],
            unique = true
        )
    ]
)
data class ShoppingListItemGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "shopping_list_id")
    val shoppingListId: Int,

    @ColumnInfo(name = "item_group_id")
    val itemGroupId: Int,

    @ColumnInfo(name = "recipe_id")
    val recipeId: Int?,

    val quantity: Int,
    val isChecked: Boolean
)
