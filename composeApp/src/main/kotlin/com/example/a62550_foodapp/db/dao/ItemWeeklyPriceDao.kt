package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.ItemWeeklyPrice
import com.example.a62550_foodapp.db.projection.ItemWithPrice

@Dao
interface ItemWeeklyPriceDao {
    @Insert
    suspend fun insert(itemWeeklyPrice: ItemWeeklyPrice)

    @Query("""
        SELECT i.id, i.name, i.size, i.unitType, i.category, i.imagePath, iwp.price
        FROM items i
        JOIN item_weekly_prices iwp ON i.id = iwp.item_id
        WHERE i.itemGroupId = :itemGroupId
    """)
    suspend fun getPricesForItemGroup(
        itemGroupId: Int
    ): List<ItemWithPrice>

}
