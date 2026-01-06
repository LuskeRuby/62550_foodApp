package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert
    suspend fun insert(recipe: Recipe)

    @Query("SELECT * FROM recipes")
    fun getAll(): Flow<List<Recipe>>
}