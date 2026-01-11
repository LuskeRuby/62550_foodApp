package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.Recipe
import kotlinx.coroutines.flow.Flow
import com.example.a62550_foodapp.db.projection.RecipeItemGroupQuantity

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<Recipe>)

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Int): Flow<Recipe?>

    @Insert
    suspend fun insert(recipe: Recipe): Long

    @Query("UPDATE recipes SET imagePath = :path WHERE id = :id")
    suspend fun updateImagePath(id: Int, path: String)

    // Added: update basic recipe fields (title, preparation time, description, instructions)
    @Query("UPDATE recipes SET title = :title, preparationTimeMinutes = :preparationTimeMinutes, description = :description, instructions = :instructions WHERE id = :id")
    suspend fun updateRecipe(id: Int, title: String, preparationTimeMinutes: Int, description: String?, instructions: String?)

}
