package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.ShoppingListItem
import com.example.a62550_foodapp.db.projection.ShoppingListEntry
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

    @Query("""
        SELECT
            i.id              AS itemId,
            i.name            AS itemName,
            i.size            AS size,
            i.unitType        AS unitType,
            sli.calc_quantity AS quantity,
            sli.is_checked    AS isChecked,
            i.imagePath       AS imagePath
        FROM shopping_list_items sli
        JOIN items i
            ON i.id = sli.item_id
        WHERE sli.shopping_list_id = :shoppingListId
    """)
    fun getEntriesForList(
        shoppingListId: Int
    ): Flow<List<ShoppingListEntry>>

    @Query("""
        UPDATE shopping_list_items
        SET is_checked = :checked
        WHERE shopping_list_id = :shoppingListId
          AND item_id = :itemId
    """)
    suspend fun updateChecked(
        shoppingListId: Int,
        itemId: Int,
        checked: Boolean
    )
}
