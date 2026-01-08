package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.RecipeItem
import com.example.a62550_foodapp.model.RecipeIngredient
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeItemDao {
    @Insert
    suspend fun insert(recipeItem: RecipeItem)

    @Query("""
        SELECT items.name, recipe_items.quantity, items.unit
        FROM items 
        INNER JOIN recipe_items ON items.id = recipe_items.item_id 
        WHERE recipe_items.recipe_id = :recipeId
    """)
    fun getIngredientsForRecipe(recipeId: Int): Flow<List<RecipeIngredient>>
}
