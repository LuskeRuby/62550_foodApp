package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.ItemWeeklyPrice
import com.example.a62550_foodapp.db.projection.ItemWithPrice

// Simple data class for holding item size and price information
data class ItemSizePrice(
    val id: Int,
    val size: Float,
    val price: Float
)

@Dao
interface ItemWeeklyPriceDao {

    @Insert
    suspend fun insert(itemWeeklyPrice: ItemWeeklyPrice)

    @Query("""
        SELECT
            i.id            AS itemId,
            i.name          AS itemName,
            ig.category     AS category,
            p.price         AS price
        FROM items i
        JOIN item_groups ig 
            ON ig.id = i.item_group_id
        JOIN item_weekly_prices p 
            ON p.item_id = i.id
        WHERE i.item_group_id = :itemGroupId
    """)
    suspend fun getPricesForItemGroup(
        itemGroupId: Int
    ): List<ItemWithPrice>

    @Query("""
        SELECT i.id, i.size, MIN(p.price) as price
        FROM items i
        JOIN item_weekly_prices p ON p.item_id = i.id
        WHERE i.item_group_id = :itemGroupId
        GROUP BY i.id, i.size
    """)
    suspend fun getItemSizesAndMinPrices(
        itemGroupId: Int
    ): List<ItemSizePrice>
}
