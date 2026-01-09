package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.ShoppingListItem
import com.example.a62550_foodapp.db.projection.ShoppingListRowDetails
import com.example.a62550_foodapp.db.projection.ShoppingListSupermarketRow
import kotlinx.coroutines.flow.Flow
@Dao
interface ShoppingListItemDao {

    @Insert
    suspend fun insert(shoppingListItem: ShoppingListItem)

    @Query("""
        SELECT
            s.id AS supermarketId,
            s.name AS supermarketName,

            i.itemgroup AS itemGroupId,
            i.id AS itemId,
            i.name AS itemName,
            i.unit AS unit,

            sli.quantity AS quantity,
            sli.is_checked AS isChecked,

            iwp.price AS price
        FROM shopping_list_items sli
        JOIN items i
            ON i.id = sli.item_id
        JOIN item_weekly_prices iwp
            ON iwp.item_id = i.id
        JOIN supermarkets s
            ON s.id = iwp.supermarket_id
        WHERE sli.shopping_list_id = (
            SELECT id FROM shopping_lists ORDER BY id ASC LIMIT 1
        )
          AND iwp.year = :year
          AND iwp.week = :week
        ORDER BY
            s.name,
            i.itemgroup,
            i.name
    """)
    fun getShoppingListBySupermarket(
        year: Int,
        week: Int
    ): Flow<List<ShoppingListSupermarketRow>>

    @Query("""
        UPDATE shopping_list_items
        SET is_checked = :checked
        WHERE shopping_list_id = (
            SELECT id FROM shopping_lists ORDER BY id ASC LIMIT 1
        )
          AND item_id = :itemId
    """)
    suspend fun updateChecked(
        itemId: Int,
        checked: Boolean
    )
}
