package com.example.a62550_foodapp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = ItemGroup::class,
            parentColumns = ["id"],
            childColumns = ["item_group_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("item_group_id")]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "item_group_id")
    val itemGroupId: Long,

    val name: String,
    val size: Float,
    val unitType: String,
    val imagePath: String? = null
)
