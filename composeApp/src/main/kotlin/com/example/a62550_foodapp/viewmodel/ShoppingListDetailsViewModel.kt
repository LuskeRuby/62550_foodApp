package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListItemDao
import com.example.a62550_foodapp.ui.shoppingList.ShoppingListEntryUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListDetailsViewModel(
    private val shoppingListId: Int,
    private val shoppingListItemDao: ShoppingListItemDao
) : ViewModel() {

    val items: StateFlow<List<ShoppingListEntryUi>> =
        shoppingListItemDao
            .getEntriesForList(shoppingListId)
            .map { entries ->
                entries.map {
                    ShoppingListEntryUi(
                        itemId = it.itemId,
                        name = it.itemName,
                        quantity = it.quantity,
                        size = it.size,
                        unitType = it.unitType,
                        isChecked = it.isChecked,
                        imagePath = it.imagePath,
                        category = it.category
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun setChecked(itemId: Int, checked: Boolean) {
        viewModelScope.launch {
            shoppingListItemDao.updateChecked(
                shoppingListId = shoppingListId,
                itemId = itemId,
                checked = checked
            )
        }
    }
}
