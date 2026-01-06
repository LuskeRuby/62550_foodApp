package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListItemDao
import com.example.a62550_foodapp.model.ShoppingListRowDisplay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AndroidShoppingListDetailsViewModel(
    shoppingListItemDao: ShoppingListItemDao
) : ViewModel(), ShoppingListDetailsViewModel {

    // For now, we are hardcoding the shopping list ID to 1
    private val listId = 1L

    override val items: StateFlow<List<ShoppingListRowDisplay>> = 
        shoppingListItemDao.getShoppingListRowDetails(listId)
            .map { detailsList ->
                detailsList.map {
                    ShoppingListRowDisplay(
                        itemName = it.itemName,
                        quantityText = "${it.quantity} ${it.unit ?: ""}".trim(),
                        isChecked = it.isChecked.toString(),
                        string = it.supermarketName ?: "N/A",
                        string1 = it.cheapestPrice?.let { "%.2f kr".format(it) } ?: "N/A",
                        bool = it.isChecked
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}
