package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.FoodItemDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.a62550_foodapp.db.entity.FoodItem as DbFoodItem
import com.example.a62550_foodapp.model.FoodItem as ModelFoodItem

class MainViewModel(private val foodItemDao: FoodItemDao) : ViewModel() {
        val foodItems: StateFlow<List<ModelFoodItem>> = foodItemDao.getAll()
        .map { dbItems ->
            dbItems.map { ModelFoodItem(it.id, it.name, it.calories) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFoodItem(name: String, calories: Int) {
        viewModelScope.launch {
            foodItemDao.insert(DbFoodItem(name = name, calories = calories))
        }
    }
}
