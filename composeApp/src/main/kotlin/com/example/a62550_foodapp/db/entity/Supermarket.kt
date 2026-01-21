package com.example.a62550_foodapp.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "supermarkets",
    indices = [Index(value = ["name"], unique = true)]
)
data class Supermarket(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val logo: String? = null
)


