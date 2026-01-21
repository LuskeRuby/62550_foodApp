package com.example.a62550_foodapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_groups")
data class ItemGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val category: String,
    @ColumnInfo(name = "unit_type")
    val unitType: String
)
