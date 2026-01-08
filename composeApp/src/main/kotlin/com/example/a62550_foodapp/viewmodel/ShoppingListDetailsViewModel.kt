package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListItemDao
import com.example.a62550_foodapp.model.ShoppingListRowDisplay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ShoppingListDetailsViewModel(
    shoppingListItemDao: ShoppingListItemDao
) : ViewModel() {

    val items: StateFlow<List<ShoppingListRowDisplay>> =
        shoppingListItemDao.getShoppingListRowDetails()
            .map { detailsList ->
                detailsList.map {
                    ShoppingListRowDisplay(
                        itemName = it.itemName,
                        quantity = it.quantity,
                        unit = it.unit,
                        isChecked = it.isChecked,
                        supermarketName = it.supermarketName,
                        cheapestPrice = it.cheapestPrice
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}

