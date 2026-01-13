package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListItemDao
import com.example.a62550_foodapp.db.entity.ShoppingListItem
import com.example.a62550_foodapp.ui.shoppingList.ShoppingListEntryUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListDetailsViewModel(
    private val shoppingListId: Int,
    private val shoppingListItemDao: ShoppingListItemDao
) : ViewModel() {

   //select a supermarket
    private val _selectedSupermarketId = MutableStateFlow(1)
    val selectedSupermarketId: StateFlow<Int> = _selectedSupermarketId

  //items
    val items: StateFlow<List<ShoppingListEntryUi>> =
        selectedSupermarketId
            .flatMapLatest { supermarketId ->
                shoppingListItemDao.getEntriesForList(
                    shoppingListId = shoppingListId,
                    supermarketId = supermarketId
                )
            }
            .map { entries ->
                entries.map {
                    ShoppingListEntryUi(
                        itemId = it.itemId,
                        name = it.itemName,
                        category = it.category,
                        quantity = it.quantity,
                        unitType = it.unitType,
                        price = it.price,
                        isChecked = it.isChecked,
                        size = it.size
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    //total price per supermarket
    val totalPrice: StateFlow<Float?> =
        selectedSupermarketId
            .flatMapLatest { supermarketId ->
                shoppingListItemDao.getTotalPrice(
                    shoppingListId = shoppingListId,
                    supermarketId = supermarketId
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    fun selectSupermarket(id: Int) {
        _selectedSupermarketId.value = id
    }

    fun addItem(itemId: Int, quantity: Int) {
        viewModelScope.launch {
            shoppingListItemDao.addItemToList(
                ShoppingListItem(
                    shoppingListId = shoppingListId,
                    itemId = itemId,
                    calcQuantity = quantity,
                    isChecked = false
                )
            )
        }
    }

    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            shoppingListItemDao.deleteItem(
                shoppingListId = shoppingListId,
                itemId = itemId
            )
        }
    }

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
