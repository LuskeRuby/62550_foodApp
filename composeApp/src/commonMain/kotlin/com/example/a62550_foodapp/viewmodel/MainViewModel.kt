package com.example.a62550_foodapp.viewmodel

import com.example.a62550_foodapp.model.Recipe
import com.example.a62550_foodapp.model.ShoppingList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

//@KoinViewModel
class MainViewModel {

    private val _recipeList = MutableStateFlow<List<Recipe>>(emptyList())
    val recipeList: StateFlow<List<Recipe>> = _recipeList.asStateFlow()

    private val _shoppingList = MutableStateFlow<ShoppingList>(ShoppingList(0, "",emptyList()))
    val shoppingList: StateFlow<ShoppingList> = _shoppingList.asStateFlow()

    init {
        loadRecipeList()
        loadShoppingList()
    }

    fun loadRecipeList(){

    }

    fun loadShoppingList(){

    }

}

