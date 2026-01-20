package com.example.a62550_foodapp.model

data class ShoppingListEntryUi(
    val itemId: Int,
    val itemGroupId: Int,
    val recipeId: Int?,

    val name: String,
    val quantity: Int,
    val unitType: String,
    val isChecked: Boolean,
    val category: String,
    val size: Float,
    val price: Float?
)