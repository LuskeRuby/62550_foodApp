package com.example.a62550_foodapp.model

// UI-facing data class
data class Recipe(
    val id: Int,
    val title: String,
    val description: String?,
    val instructions: String?,
    val picture: ByteArray?,
    val deletable: Boolean?,
    val items: List<RecipeItem> = emptyList()
)
