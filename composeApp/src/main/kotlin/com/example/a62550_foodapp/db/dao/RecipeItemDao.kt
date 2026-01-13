package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.RecipeItem

@Dao
interface RecipeItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipeItem: RecipeItem)

    @Query("DELETE FROM recipe_items WHERE recipe_id = :recipeId")
    suspend fun deleteForRecipe(recipeId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RecipeItem>)

    @Query("""
        SELECT *
        FROM recipe_items
        WHERE recipe_id = :recipeId
    """)
    suspend fun getItemsForRecipe(
        recipeId: Int
    ): List<RecipeItem>
}