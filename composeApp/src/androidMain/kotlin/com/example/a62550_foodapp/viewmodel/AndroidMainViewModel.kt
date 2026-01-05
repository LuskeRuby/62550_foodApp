package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.a62550_foodapp.viewmodel.MainViewModel as CommonMainViewModel

class AndroidMainViewModel : ViewModel() {

    private val common = CommonMainViewModel()

    val recipeList = common.recipeList
    val shoppingList = common.shoppingList

    fun loadRecipeList() = common.loadRecipeList()
    fun loadShoppingList() = common.loadShoppingList()
}
