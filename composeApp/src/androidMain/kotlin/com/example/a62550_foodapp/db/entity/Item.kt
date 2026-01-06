package com.example.a62550_foodapp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itemgroup: Int?,
    val name: String,
    val unit: String?,
    val picture: ByteArray?
)