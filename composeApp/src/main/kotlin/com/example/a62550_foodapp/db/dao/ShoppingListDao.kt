package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.a62550_foodapp.db.entity.ShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    // CRUD operations for ShoppingList entity
    @Insert
    suspend fun insert(shoppingList: ShoppingList): Long

    @Query("SELECT * FROM shopping_lists")
    fun getAll(): Flow<List<ShoppingList>>

    @Update
    suspend fun update(shoppingList: ShoppingList): Int

    @Delete
    suspend fun delete(shoppingList: ShoppingList)
}
