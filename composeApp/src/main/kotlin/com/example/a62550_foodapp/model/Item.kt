package com.example.a62550_foodapp.model

/**
 * UI-facing representation of a concrete product
 */
data class Item(
    val id: Int,
    val itemGroupId: Int,
    val category: String,
    val name: String,
    val size: Float,
    val unitType: String,
    val imagePath: String?
)
