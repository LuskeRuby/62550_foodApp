package com.example.a62550_foodapp.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "recipe_items",
    primaryKeys = ["recipe_id", "item_id"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"]
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"]
        )
    ]
)
data class RecipeItem(
    val recipe_id: Int,
    val item_id: Int,
    val quantity: Float
)