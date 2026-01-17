package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListItemDao
import com.example.a62550_foodapp.db.entity.ShoppingListItem
import com.example.a62550_foodapp.db.projection.ItemWithPriceAndCategory
import com.example.a62550_foodapp.model.ShoppingListEntryUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    @OptIn(ExperimentalCoroutinesApi::class)
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
    data class TotalUi(
        val total: Float,
        val missingCount: Int
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalUi: StateFlow<TotalUi> =
        items.map { list ->
            val missing = list.count { it.price == null }

            val sum = list.sumOf {
                ((it.price ?: 0f) * it.quantity).toDouble()
            }.toFloat()

            TotalUi(
                total = sum,
                missingCount = missing
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TotalUi(0f, 0)
        )

    fun selectSupermarket(id: Int) {
        _selectedSupermarketId.value = id
    }

    fun addItem(itemId: Int, quantity: Int) {
        viewModelScope.launch {
            try {
                shoppingListItemDao.addItemToList(
                    ShoppingListItem(
                        shoppingListId = shoppingListId,
                        itemId = itemId,
                        calcQuantity = quantity,
                        isChecked = false
                    )
                )
            } catch (e: Exception) {
                // Handle exception (e.g., log it)
                e.printStackTrace()
            }
        }
    }

    fun addItem(items: List<ShoppingListEntryUi>) {
        viewModelScope.launch {
            try {
                val newEntry = items.map { entry ->
                    ShoppingListItem(
                        shoppingListId = shoppingListId,
                        itemId = entry.itemId,
                        calcQuantity = entry.quantity,
                        isChecked = false
                    )
                }
                shoppingListItemDao.addItemsToList(newEntry)

            } catch (e: Exception) {
                // Handle exception (e.g., log it)
                e.printStackTrace()
            }
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

    // temp list kept in memory (not in DB)
    private val _tempItemsList  = MutableStateFlow<List<ShoppingListEntryUi>>(emptyList())
    val tempItemsList: StateFlow<List<ShoppingListEntryUi>> = _tempItemsList

    fun addTempItem(item: ItemWithPriceAndCategory) {

            val itemToShoppingListEntryUi = ShoppingListEntryUi(
                itemId = item.item.id,
                name = item.item.name,
                quantity = 1,
                unitType = item.item.unitType,
                isChecked = false,
                category = item.itemGroup.category,   // <-- problem
                size = item.item.size,
                price = item.weeklyPrices
                    .maxWithOrNull(compareBy({ it.year }, { it.week }))
                    ?.price
            )

            _tempItemsList.value += itemToShoppingListEntryUi
    }

    fun removeTempItem(itemId: Int) {
        _tempItemsList.value = _tempItemsList.value.filter { it.itemId != itemId }
    }

    fun clearTempItems() {
        _tempItemsList.value = emptyList()
    }

}
