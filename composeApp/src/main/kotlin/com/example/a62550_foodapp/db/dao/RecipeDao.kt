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
    fun getRecipeById(id: Long): Flow<Recipe?>

    @Insert
    suspend fun insert(recipe: Recipe): Long

    @Query("UPDATE recipes SET image_path = :path WHERE id = :id")
    suspend fun updateImagePath(id: Long, path: String?)

    @Query("""
        UPDATE recipes 
        SET title = :title, 
            preparation_time_minutes = :preparationTimeMinutes, 
            description = :description, 
            instructions = :instructions 
        WHERE id = :id
    """)
    suspend fun updateRecipe(
        id: Long,
        title: String,
        preparationTimeMinutes: Int,
        description: String,
        instructions: String
    )
}
