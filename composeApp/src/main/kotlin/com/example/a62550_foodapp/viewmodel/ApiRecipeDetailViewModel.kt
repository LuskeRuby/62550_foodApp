package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.api.MealDbApi
import com.example.a62550_foodapp.api.dto.MealDto
import com.example.a62550_foodapp.api.dto.toApiIngredients
import com.example.a62550_foodapp.db.dao.ItemGroupDao
import com.example.a62550_foodapp.db.dao.ShoppingListItemGroupDao
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.db.entity.ShoppingListItemGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ApiRecipeDetailViewModel(
    private val api: MealDbApi,
    private val itemGroupDao: ItemGroupDao,
    private val shoppingListItemGroupDao: ShoppingListItemGroupDao
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


    private fun normalizeIngredientName(name: String): String =
        name.lowercase().trim().replace(Regex("\\s+"), " ")

    private suspend fun resolveOrCreateItemGroup(
        apiName: String,
        apiMeasure: String?
    ): ItemGroup {

        val normalizedApiName = normalizeIngredientName(apiName)

        val existing = itemGroupDao.getAllItemGroups()
            .first()
            .firstOrNull {
                normalizeIngredientName(it.name) == normalizedApiName
            }

        if (existing != null) return existing

        val unitType =
            apiMeasure?.takeIf { it.isNotBlank() } ?: "ukendt"

        val newId = itemGroupDao.insert(
            ItemGroup(
                name = apiName.trim(),
                category = "Utilgængelige varer",
                unitType = unitType
            )
        )

        return ItemGroup(
            id = newId,
            name = apiName.trim(),
            category = "Utilgængelige varer",
            unitType = unitType
        )
    }

    fun addMealToShoppingList(
        shoppingListId: Long,
        meal: MealDto
    ) {
        viewModelScope.launch {

            val apiIngredients = meal.toApiIngredients()
                .filter { it.name.isNotBlank() }

            if (apiIngredients.isEmpty()) return@launch

            val existingEntries =
                shoppingListItemGroupDao
                    .getItemGroupsMatchingListId(shoppingListId)
                    .first()
                    .associateBy { it.itemGroupId }


            for (apiIng in apiIngredients) {

                val group = resolveOrCreateItemGroup(
                    apiName = apiIng.name,
                    apiMeasure = apiIng.measure
                )

                val existing = existingEntries[group.id]

                if (existing != null) {
                    // increment quantity
                    shoppingListItemGroupDao.update(
                        existing.copy(
                            portionQuantity = existing.portionQuantity + 1
                        )
                    )
                } else {
                    // insert new row
                    shoppingListItemGroupDao.insert(
                        ShoppingListItemGroup(
                            shoppingListId = shoppingListId,
                            itemGroupId = group.id,
                            recipeId = null,
                            portionQuantity = 1,
                            portionSize = 1f,
                            isChecked = false
                        )
                    )
                }

            }



        }
    }


}