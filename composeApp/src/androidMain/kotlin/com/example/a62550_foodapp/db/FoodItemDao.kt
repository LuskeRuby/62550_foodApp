package com.example.a62550_foodapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Insert
    suspend fun insert(foodItem: FoodItem)

    @Query("SELECT * FROM food_items")
    fun getAll(): Flow<List<FoodItem>>
}
