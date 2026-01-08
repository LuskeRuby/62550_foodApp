package com.example.a62550_foodapp.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "recipe_items",
    primaryKeys = ["recipe_id", "item_group_id"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecipeItem(
    val recipe_id: Int,
    val item_group_id: Int,
    val quantity: Float
)