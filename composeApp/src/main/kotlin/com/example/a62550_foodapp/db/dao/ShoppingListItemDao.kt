package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.ShoppingListItem

import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListItemDao {

    @Insert
    suspend fun insert(item: ShoppingListItem)

    @Query("""
        SELECT *
        FROM shopping_list_items
        WHERE shopping_list_id = :shoppingListId
    """)
    fun getItemsForList(
        shoppingListId: Int
    ): Flow<List<ShoppingListItem>>

}
