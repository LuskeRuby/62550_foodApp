package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {

    @Insert
    suspend fun insert(foodItem: FoodItem): Long

    @Query("SELECT * FROM food_items")
    fun getAll(): Flow<List<FoodItem>>
}