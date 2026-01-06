package com.example.a62550_foodapp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String?,
    val instructions: String?,
    val picture: ByteArray?,
    val deletable: Boolean?
)