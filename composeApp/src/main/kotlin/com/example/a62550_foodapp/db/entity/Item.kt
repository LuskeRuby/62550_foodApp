package com.example.a62550_foodapp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    /**
     * Alternatives to that specific item. e.g. fløde 250ml & 500ml
     */
    val itemGroupId: Int,   // logical ingredient (alternatives)
    val category: String,   // Mejeri, Frugt & grønt, osv.

    val name: String,       // "Arla Fløde 38% 250 ml"
    val size: Float,        // 250
    val unitType: String,   // "ml"
    val imagePath: String? = null
)