package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.api.MealDbApi
import com.example.a62550_foodapp.model.MealCategory
import com.example.a62550_foodapp.model.MealSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiscoverRecipeViewModel(
    private val api: MealDbApi
) : ViewModel() {

    private val _categories = MutableStateFlow<List<MealCategory>>(emptyList())
    val categories: StateFlow<List<MealCategory>> = _categories

    private val _meals = MutableStateFlow<List<MealSummary>>(emptyList())
    val meals: StateFlow<List<MealSummary>> = _meals

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadCategories() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val response = api.getCategories()
                _categories.value = response.categories.map {
                    MealCategory(
                        id = it.idCategory,
                        name = it.strCategory,
                        thumbnail = it.strCategoryThumb,
                        description = it.strCategoryDescription
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    fun selectCategory(category: String) {
        _selectedCategory.value = category

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val response = api.getMealsByCategory(category)
                _meals.value = response.meals.map {
                    MealSummary(
                        id = it.idMeal,
                        name = it.strMeal,
                        thumbnail = it.strMealThumb
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }


}
