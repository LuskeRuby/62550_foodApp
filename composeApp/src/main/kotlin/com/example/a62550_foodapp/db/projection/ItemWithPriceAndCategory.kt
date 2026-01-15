package com.example.a62550_foodapp.db.projection

import androidx.room.Embedded
import androidx.room.Relation
import com.example.a62550_foodapp.db.entity.Item
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.db.entity.ItemWeeklyPrice

data class ItemWithPriceAndCategory(
    @Embedded val item: Item,
    @Relation(
        parentColumn = "id",
        entityColumn = "item_id"
    )
    val weeklyPrices: List<ItemWeeklyPrice>,
    @Relation(
        parentColumn = "item_group_id",
        entityColumn = "id"
    )
    val itemGroup: ItemGroup
)