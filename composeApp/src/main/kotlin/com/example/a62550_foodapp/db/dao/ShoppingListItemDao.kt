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

    @Insert
    suspend fun addItemToList(shoppingListItem: ShoppingListItem): Long

    @Insert
    suspend fun addItemsToList(shoppingListItems: List<ShoppingListItem>): List<Long>

    @Query("""
        SELECT *
        FROM shopping_list_items
        WHERE shopping_list_id = :shoppingListId
    """)
    fun getItemsForList(
        shoppingListId: Int
    ): Flow<List<ShoppingListItem>>

    //shoppinglist data
    @Query("""
    SELECT
    i.id              AS itemId,
    i.name            AS itemName,
    i.size            AS size,
    i.unitType        AS unitType,
    sli.calc_quantity AS quantity,
    p.price           AS price,
    sli.is_checked    AS isChecked,
    ig.category       AS category
FROM shopping_list_items sli
JOIN items i ON i.id = sli.item_id
JOIN item_groups ig ON ig.id = i.item_group_id
LEFT JOIN item_weekly_prices p
    ON p.item_id = i.id
   AND p.supermarket_id = :supermarketId
WHERE sli.shopping_list_id = :shoppingListId

""")
    fun getEntriesForList(
        shoppingListId: Int,
        supermarketId: Int
    ): Flow<List<ShoppingListEntry>>

    //get price
    @Query("""
    SELECT SUM(
        CASE 
            WHEN p.price IS NOT NULL 
            THEN p.price * sli.calc_quantity
            ELSE 0
        END
    )
    FROM shopping_list_items sli
    LEFT JOIN item_weekly_prices p
        ON p.item_id = sli.item_id
       AND p.supermarket_id = :supermarketId
    WHERE sli.shopping_list_id = :shoppingListId
""")
    fun getTotalPrice(
        shoppingListId: Int,
        supermarketId: Int
    ): Flow<Float?>



    //update checkmark
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

    @Query("""
        DELETE FROM shopping_list_items
        WHERE shopping_list_id = :shoppingListId
        AND item_id = :itemId
    """)
    suspend fun deleteItem(
        shoppingListId: Int,
        itemId: Int
    )

}
