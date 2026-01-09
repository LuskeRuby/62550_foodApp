package com.example.a62550_foodapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "recipe_items",
    primaryKeys = ["recipe_id", "item_id"]
)
data class RecipeItem(
    @ColumnInfo(name = "recipe_id")
    val recipeId: Int,

    @ColumnInfo(name = "item_id")
    val itemId: Int,

    val quantity: Float
)
