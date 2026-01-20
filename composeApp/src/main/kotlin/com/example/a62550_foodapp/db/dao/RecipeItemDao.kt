package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.RecipeItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeItemDao {

    @Insert
    suspend fun insert(item: RecipeItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RecipeItem>)

    @Query("DELETE FROM recipe_items WHERE recipe_id = :recipeId")
    suspend fun deleteForRecipe(recipeId: Int)

    // one-shot load (used when adding to shopping list)
    @Query("""
        SELECT *
        FROM recipe_items
        WHERE recipe_id = :recipeId
    """)
    suspend fun getItemsForRecipe(recipeId: Int): List<RecipeItem>

    // reactive load (used in UI)
    @Query("""
        SELECT *
        FROM recipe_items
        WHERE recipe_id = :recipeId
    """)
    fun getItemsForRecipeFlow(recipeId: Int): Flow<List<RecipeItem>>
}