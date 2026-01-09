package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.a62550_foodapp.db.dao.ItemDao
import com.example.a62550_foodapp.db.dao.ShoppingListItemDao
import com.example.a62550_foodapp.model.ShoppingListRowDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ShoppingListDetailsViewModel(
    private val shoppingListItemDao: ShoppingListItemDao,
    private val itemDao: ItemDao
) : ViewModel() {

    // TEMPORARILY DISABLED after schema refactor
    // Old logic depended on:
    // - Item.category
    // - ItemDao.getPricesForItemGroup()
    // - quantity vs calcQuantity mismatch
    //
    // Will be reimplemented using:
    // ShoppingListItem + Item + ItemGroup + ItemWeeklyPrice

    val items: StateFlow<List<ShoppingListRowDisplay>> =
        MutableStateFlow(emptyList())
}
