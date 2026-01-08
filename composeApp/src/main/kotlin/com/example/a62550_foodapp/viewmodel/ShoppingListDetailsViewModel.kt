package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ItemDao
import com.example.a62550_foodapp.db.dao.ShoppingListItemDao
import com.example.a62550_foodapp.model.ShoppingListRowDisplay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ShoppingListDetailsViewModel(
    private val shoppingListItemDao: ShoppingListItemDao,
    private val itemDao: ItemDao
) : ViewModel() {

    val items: StateFlow<List<ShoppingListRowDisplay>> =
        shoppingListItemDao.getItemsForList(shoppingListId = 1) // midlertidigt hardcoded
            .map { shoppingItems ->

                shoppingItems.mapNotNull { shoppingItem ->

                    // 1Find itemGroupId
                    val baseItem = itemDao.getItemById(shoppingItem.itemId)
                        ?: return@mapNotNull null

                    // Find alle alternativer + priser
                    val alternatives =
                        itemDao.getPricesForItemGroup(baseItem.itemGroupId)

                    // Find billigste
                    val cheapest = alternatives.minByOrNull { it.price }
                        ?: return@mapNotNull null

                    // Map til UI-model
                    ShoppingListRowDisplay(
                        itemName = cheapest.name,
                        quantity = shoppingItem.quantity,
                        unitType = cheapest.unitType,
                        size = cheapest.size,
                        category = cheapest.category,
                        imagePath = cheapest.imagePath,
                        isChecked = shoppingItem.isChecked
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}