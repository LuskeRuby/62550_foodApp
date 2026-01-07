package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.a62550_foodapp.db.entity.RecipeItem

@Dao
interface RecipeItemDao {
    @Insert
    suspend fun insert(recipeItem: RecipeItem)
}