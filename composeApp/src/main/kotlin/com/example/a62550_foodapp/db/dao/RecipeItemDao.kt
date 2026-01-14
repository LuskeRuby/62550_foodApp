package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.RecipeItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RecipeItem>)

    @Query("DELETE FROM recipe_items WHERE recipe_id = :recipeId")
    suspend fun deleteForRecipe(recipeId: Int)

    @Query("""
        SELECT *
        FROM recipe_items
        WHERE recipe_id = :recipeId
    """)
    suspend fun getItemsForRecipe(
        recipeId: Int
    ): List<RecipeItem>

    // for reactive updating
    @Query("""
    SELECT *
    FROM recipe_items
    WHERE recipe_id = :recipeId
""")
    fun getItemsForRecipeFlow(
        recipeId: Int
    ): Flow<List<RecipeItem>>

}