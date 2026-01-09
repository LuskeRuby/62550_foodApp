package com.example.a62550_foodapp.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithItems(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipe_id"
    )
    val items: List<RecipeItem>
)
