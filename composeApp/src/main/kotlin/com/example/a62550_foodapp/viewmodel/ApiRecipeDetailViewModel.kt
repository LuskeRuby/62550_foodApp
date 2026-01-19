package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.api.MealDbApi
import com.example.a62550_foodapp.api.dto.MealDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApiRecipeDetailViewModel(
    private val api: MealDbApi
) : ViewModel() {

    private val _meal = MutableStateFlow<MealDto?>(null)
    val meal: StateFlow<MealDto?> = _meal

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun load(mealId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _meal.value = api.getMealById(mealId).meals.firstOrNull()
            } finally {
                _loading.value = false
            }
        }
    }
}