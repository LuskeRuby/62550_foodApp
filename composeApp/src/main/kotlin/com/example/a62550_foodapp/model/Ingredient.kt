package com.example.a62550_foodapp.model

//storespecific item if possible, otherwise fallback to itemgroup
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
