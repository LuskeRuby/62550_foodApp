package com.example.a62550_foodapp.db.entity

import androidx.room.Entity

@Entity(
    tableName = "item_supermarket_cross_ref",
    primaryKeys = ["itemId", "supermarketId"]
)
data class ItemSupermarketCrossRef(
    val itemId: Int,
    val supermarketId: Int
)
