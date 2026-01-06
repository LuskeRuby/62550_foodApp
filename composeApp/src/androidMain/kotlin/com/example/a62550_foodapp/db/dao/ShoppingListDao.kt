package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.ShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Insert
    suspend fun insert(shoppingList: ShoppingList)

    @Query("SELECT * FROM shopping_lists")
    fun getAll(): Flow<List<ShoppingList>>
}