package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListItemDao
import com.example.a62550_foodapp.db.dao.ShoppingListItemGroupDao
import com.example.a62550_foodapp.db.entity.ShoppingListItem
import com.example.a62550_foodapp.db.entity.ShoppingListItemGroup
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
import kotlin.Int
import kotlin.collections.map
import kotlin.text.insert

class ShoppingListDetailsViewModel(
    private val shoppingListId: Int,
    private val shoppingListItemDao: ShoppingListItemDao,
    private val shoppingListItemGroupDao: ShoppingListItemGroupDao
) : ViewModel() {

    // Shopping List Item Groups
    // --------------------------------------------------------------------------------
    private val itemGroupList: StateFlow<List<ShoppingListItemGroup>> =
        shoppingListItemGroupDao.getItemGroupsMatchingListId(shoppingListId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // TODO handle duplicate items
    fun addItemGroup(addedItem: ShoppingListEntryUi) {
        viewModelScope.launch {

            val mappedItem = addedItem.map { entry: ShoppingListEntryUi ->
                ShoppingListItemGroup(
                    id = 0,
                    shoppingListId = shoppingListId,
                    itemGroupId = entry.itemGroupId,
                    recipeId = entry.recipeId,
                    quantity = entry.quantity,
                    isChecked = false
                )
            }


            val (updateEntryTemp, newEntry) = mappedItem.partition { mappedItem ->
                itemGroupList.value.any { db -> mappedItem.itemId == db.itemId }
            }

            try {
                shoppingListItemGroupDao.insert(newEntry)
                updateItemGroup(updateEntry)
            } catch (e: Exception) {
                // Handle exception (e.g., log it)
                e.printStackTrace()
            }

        }
    }



    // TODO make proper update instead of replace
    fun updateItemGroup(itemToUpdate: ShoppingListEntryUi) {
        viewModelScope.launch {

            val previousQuantity = itemGroupList.value.first { it.itemId == itemToUpdate.itemId }.quantity

            val updateEntry =
                ShoppingListItemGroup(
                    id = ,
                    shoppingListId = itemToUpdate.shoppingListId,
                    itemGroupId = itemToUpdate.itemId,
                    recipeId = itemToUpdate.recipeId,
                    quantity = itemToUpdate.quantity + previousQuantity,
                    isChecked = itemToUpdate.isChecked
                )

            shoppingListItemGroupDao.update(updateEntry)
        }
    }

    fun deleteItemGroup(item: ShoppingListItemGroup) {
        viewModelScope.launch {
            shoppingListItemGroupDao.delete(item)
        }
    }

    fun setCheckedItemGroup(
        itemGroupId: Int,
        recipeId: Int?,
        checked: Boolean
    ) {
        viewModelScope.launch {
            shoppingListItemGroupDao.updateCheckmark(
                shoppingListId = shoppingListId,
                itemGroupId = itemGroupId,
                recipeId = recipeId,
                checked = checked
            )
        }
    }

    // UI entries for Shopping List
    // --------------------------------------------------------------------------------

    //TODO
    //val uiItems: List<> = Dao.get...().map { ... }


    // liste af billigste items per group


    // vi har en liste af item groups
    // vi vil vise en liste af billigste items der findes i DB

    // kald funktion for hver itemGroup der giver billigste item
    // for selected supermarket
















    // Replace everything below this line
    // --------------------------------------------------------------------------------


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
                shoppingListItemDao.insert(
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

    fun addItem(addedItems: List<ShoppingListEntryUi>) {
        viewModelScope.launch {

            val mappedItems = addedItems.map { entry ->
                ShoppingListItem(
                    shoppingListId = shoppingListId,
                    itemId = entry.itemId,
                    calcQuantity = entry.quantity,
                    isChecked = false
                )
            }

            val (updateEntryTemp, newEntry) = mappedItems.partition { mappedItem ->
                items.value.any { db -> mappedItem.itemId == db.itemId }
            }

            val updateEntry = updateEntryTemp.map { mappedItem ->
                val previousQuantity = items.value.first { it.itemId == mappedItem.itemId }.quantity
                ShoppingListItem(
                    shoppingListId = mappedItem.shoppingListId,
                    itemId = mappedItem.itemId,
                    calcQuantity = mappedItem.calcQuantity + previousQuantity,
                    isChecked = mappedItem.isChecked
                )
            }

            try {
                shoppingListItemDao.insert(newEntry)
                shoppingListItemDao.update(updateEntry)
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

        val latestPrice =
            item.weeklyPrices
                .maxWithOrNull(compareBy({ it.year }, { it.week }))
                ?.price
                ?: 0f

        val entry = ShoppingListEntryUi(
            itemId = item.item.id,
            name = item.item.name,
            quantity = 1,
            unitType = item.item.unitType,
            isChecked = false,
            category = item.itemGroup.category,
            size = item.item.size,
            price = latestPrice
        )

        val alreadyAdded =
            _tempItemsList.value.any { it.itemId == item.item.id }

        if (!alreadyAdded) {
            _tempItemsList.value = _tempItemsList.value + entry
        }
    }

    fun removeTempItem(itemId: Int) {
        _tempItemsList.value = _tempItemsList.value.filter { it.itemId != itemId }
    }

    fun clearTempItems() {
        _tempItemsList.value = emptyList()
    }

}
