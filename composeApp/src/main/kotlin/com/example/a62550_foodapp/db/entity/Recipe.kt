package com.example.a62550_foodapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    @ColumnInfo(name = "preparation_time_minutes")
    val preparationTimeMinutes: Int,
    val description: String?,
    val instructions: String?,
    // This stores the internal path: e.g., "/data/user/0/.../recipe_123.jpg"
    @ColumnInfo(name = "image_path")
    val imagePath: String? = null,
    val deletable: Boolean?
)