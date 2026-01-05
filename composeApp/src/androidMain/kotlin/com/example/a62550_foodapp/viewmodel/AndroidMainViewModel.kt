package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.FoodItemDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.a62550_foodapp.db.FoodItem as DbFoodItem
import com.example.a62550_foodapp.model.FoodItem as ModelFoodItem

class AndroidMainViewModel(private val foodItemDao: FoodItemDao) : ViewModel(), MainViewModel {

    override val foodItems: StateFlow<List<ModelFoodItem>> = foodItemDao.getAll()
        .map { dbItems ->
            dbItems.map { ModelFoodItem(it.id, it.name, it.calories) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    override fun addFoodItem(name: String, calories: Int) {
        viewModelScope.launch {
            foodItemDao.insert(DbFoodItem(name = name, calories = calories))
        }
    }
}
