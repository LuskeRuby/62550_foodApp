package com.example.a62550_foodapp.model

data class Recipe(
    val id: Long,
    val title: String,
    val preparationTimeMinutes: Int,
    val description: String?,
    val instructions: String?,
    val imagePath: String?,
    val deletable: Boolean
)
