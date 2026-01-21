package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.ItemWeeklyPrice
import com.example.a62550_foodapp.db.projection.ItemWithPrice
import kotlinx.coroutines.flow.Flow

// Simple data class for holding item size and price information
data class ItemSizePrice(
    val id: Long,
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
        itemGroupId: Long
    ): List<ItemWithPrice>

    @Query("""
        SELECT i.id, i.size, MIN(p.price) as price
        FROM items i
        JOIN item_weekly_prices p ON p.item_id = i.id
        WHERE i.item_group_id = :itemGroupId
        GROUP BY i.id, i.size
    """)
    suspend fun getItemSizesAndMinPrices(
        itemGroupId: Long
    ): List<ItemSizePrice>

    @Query("""
    SELECT i.id, i.size, MIN(p.price) as price
    FROM items i
    JOIN item_weekly_prices p ON p.item_id = i.id
    WHERE i.item_group_id = :itemGroupId
    GROUP BY i.id, i.size
""")
    fun getItemSizesAndMinPricesFlow(
        itemGroupId: Long
    ): Flow<List<ItemSizePrice>>

    @Query("""
        SELECT i.id, i.size, MIN(p.price) as price
        FROM items i
        JOIN item_weekly_prices p ON p.item_id = i.id
        WHERE i.item_group_id = :itemGroupId
        AND p.supermarket_id = :supermarketId
        GROUP BY i.id, i.size
    """)
    suspend fun getItemSizesAndMinPricesByStore(
        itemGroupId: Long,
        supermarketId: Long
    ): List<ItemSizePrice>

    @Query("""
        SELECT i.id, i.size, MIN(p.price) as price
        FROM items i
        JOIN item_weekly_prices p ON p.item_id = i.id
        WHERE i.item_group_id = :itemGroupId
        AND p.supermarket_id = :supermarketId
        GROUP BY i.id, i.size
    """)
    fun getItemSizesAndMinPricesByStoreFlow(
        itemGroupId: Long,
        supermarketId: Long
    ): Flow<List<ItemSizePrice>>

    @Query("""
        SELECT i.id, i.size, MIN(p.price) as price
        FROM items i
        JOIN item_weekly_prices p ON p.item_id = i.id
        WHERE i.item_group_id = :itemGroupId
        AND p.supermarket_id IN (:supermarketIds)
        GROUP BY i.id, i.size
    """)
    fun getItemSizesAndMinPricesByStoresFlow(
        itemGroupId: Long,
        supermarketIds: List<Long>
    ): Flow<List<ItemSizePrice>>


    @Query("""
    SELECT i.id, i.size, MIN(p.price) as price
    FROM items i
    JOIN item_weekly_prices p ON p.item_id = i.id
    WHERE i.item_group_id = :itemGroupId
      AND p.supermarket_id IN (:supermarketIds)
    GROUP BY i.id, i.size
""")
    suspend fun getItemSizesAndMinPricesByStores(
        itemGroupId: Long,
        supermarketIds: List<Long>
    ): List<ItemSizePrice>

}
