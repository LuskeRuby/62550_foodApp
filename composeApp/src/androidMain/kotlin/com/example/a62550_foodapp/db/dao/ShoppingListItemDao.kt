package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.ShoppingListItem
import com.example.a62550_foodapp.db.projection.ShoppingListRowDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListItemDao {
    @Insert
    suspend fun insert(shoppingListItem: ShoppingListItem)

    @Query("""
        SELECT
            i.name AS itemName,
            sli.quantity AS quantity,
            i.unit AS unit,
            sli.is_checked AS isChecked,
            i.itemgroup AS itemGroupId,
            (SELECT MIN(iwp.price) FROM item_weekly_prices iwp WHERE iwp.item_id = i.id) AS cheapestPrice,
            (SELECT s.name FROM supermarkets s
                INNER JOIN item_weekly_prices iwp ON s.id = iwp.supermarket_id
                WHERE iwp.item_id = i.id
                ORDER BY iwp.price ASC
                LIMIT 1
            ) AS supermarketName
        FROM shopping_list_items sli
        JOIN items i ON sli.item_id = i.id
        WHERE sli.shopping_list_id = :listId
    """)
    fun getShoppingListRowDetails(listId: Long): Flow<List<ShoppingListRowDetails>>
}
