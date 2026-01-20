package com.example.a62550_foodapp.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.a62550_foodapp.db.entity.Supermarket
import androidx.room.Index

@Entity(
    tableName = "item_weekly_prices",
    primaryKeys = ["item_id", "year", "week", "supermarket_id"],
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"]
        ),
        ForeignKey(
            entity = Supermarket::class,
            parentColumns = ["id"],
            childColumns = ["supermarket_id"]
        )
    ],
    indices = [
        Index("item_id"),
        Index("supermarket_id")
    ]
)
//TODO Rename variables
data class ItemWeeklyPrice(
    val item_id: Long,
    val supermarket_id: Long,
    val year: Int,
    val week: Int,
    val price: Float
)
