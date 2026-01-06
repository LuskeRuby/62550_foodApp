package com.example.a62550_foodapp.viewmodel

import com.example.a62550_foodapp.model.FoodItem
import kotlinx.coroutines.flow.StateFlow

interface MainViewModel {
    val foodItems: StateFlow<List<FoodItem>>

    fun addFoodItem(name: String, calories: Int)
}
