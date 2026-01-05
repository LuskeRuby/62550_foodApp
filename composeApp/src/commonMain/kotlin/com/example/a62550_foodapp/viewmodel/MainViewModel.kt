package com.example.a62550_foodapp.viewmodel

import com.example.a62550_foodapp.model.Recipe
import com.example.a62550_foodapp.model.ShoppingList

class MainViewModel {

    var recipeList: List<Recipe> = emptyList()
    val shoppingList: ShoppingList = ShoppingList(id = 0, name = "", items = emptyList())

    init {
        loadRecipeList()
        loadShoppingList()
    }

    fun loadRecipeList(){

    }

    fun loadShoppingList(){

    }

}

