package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<Recipe>)

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Int): Flow<Recipe?>

    @Query("""
        SELECT SUM(recipe_items.quantity * COALESCE(
            (SELECT price FROM item_weekly_prices 
             WHERE item_id = recipe_items.item_id 
             ORDER BY year DESC, week DESC LIMIT 1), 0))
        FROM recipe_items
        WHERE recipe_items.recipe_id = :recipeId
    """)
    fun getRecipeTotalPrice(recipeId: Int): Flow<Float?>

    @Insert
    suspend fun insert(recipe: Recipe): Long

    @Query("UPDATE recipes SET imagePath = :path WHERE id = :id")
    suspend fun updateImagePath(id: Int, path: String)
}
