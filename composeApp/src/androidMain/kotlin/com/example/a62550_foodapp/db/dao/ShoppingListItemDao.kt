package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.a62550_foodapp.db.entity.ShoppingListItem

@Dao
interface ShoppingListItemDao {
    @Insert
    suspend fun insert(shoppingListItem: ShoppingListItem)
}