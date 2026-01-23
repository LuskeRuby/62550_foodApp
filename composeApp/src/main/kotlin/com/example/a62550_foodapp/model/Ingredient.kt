package com.example.a62550_foodapp.model

//store specific item if possible, otherwise fallback to itemGroup
data class Ingredient(

    // canonical ingredient
    val itemGroupId: Int,
    val groupName: String,
    val unitType: String,

    val itemId: Int?,
    val itemName: String?,
    val itemSize: Float?,

    val price: Float?,

    val quantity: Int
)
