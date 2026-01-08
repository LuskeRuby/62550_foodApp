package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.RecipeItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeItemDao {
    @Insert
    suspend fun insert(recipeItem: RecipeItem)

    @Query("""
        SELECT recipe_items.quantity || ' ' || items.unit || ' ' || items.name 
        FROM items 
        INNER JOIN recipe_items ON items.id = recipe_items.item_id 
        WHERE recipe_items.recipe_id = :recipeId
    """)
    fun getIngredientsForRecipe(recipeId: Int): Flow<List<String>>
}
