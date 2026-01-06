package com.example.a62550_foodapp.model

// UI-facing data class
data class ItemWeeklyPrice(
    val item_id: Int,
    val year: Int,
    val week: Int,
    val price: Float
)
