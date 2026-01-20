package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListItemGroupDao
import com.example.a62550_foodapp.db.entity.ShoppingListItemGroup
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
    private val shoppingListItemGroupDao: ShoppingListItemGroupDao
) : ViewModel() {

    // Shopping List Item Groups (raw logical ingredients)
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
            shoppingListItemGroupDao.deleteItem(
                shoppingListId = item.shoppingListId,
                itemGroupId = item.itemGroupId,
                recipeId = item.recipeId
            )
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

    // UI entries for Shopping List(resolved to cheapest store items)
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

    // select a supermarket (used as filter for price queries)
    private val _selectedSupermarketId = MutableStateFlow(1)
    val selectedSupermarketId: StateFlow<Int> = _selectedSupermarketId

    fun selectSupermarket(id: Int) {
        _selectedSupermarketId.value = id
    }

    // Shopping list entries resolved to concrete items for selected store
    @OptIn(ExperimentalCoroutinesApi::class)
    val items: StateFlow<List<ShoppingListEntryUi>> =
        selectedSupermarketId
            .flatMapLatest { supermarketId ->
                shoppingListItemGroupDao.getShoppingListEntriesByStoreFlow(
                    shoppingListId = shoppingListId,
                    supermarketId = supermarketId
                )
            }
            .map { entries ->
                entries.map {
                    ShoppingListEntryUi(
                        itemId = it.itemId,
                        itemGroupId = it.itemGroupId,
                        recipeId = it.recipeId,
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

    // Total price per supermarket
    // --------------------------------------------------------------------------------

    data class TotalUi(
        val total: Float,
        val missingCount: Int
    )

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

    // Manual add (not from recipe) → goes into shopping_list_item_groups with recipeId = null
    // --------------------------------------------------------------------------------

    fun addManualItem(itemGroupId: Int, quantity: Int) {
        viewModelScope.launch {

            val existing = shoppingListItemGroupDao.getOneForRecipe(
                shoppingListId = shoppingListId,
                itemGroupId = itemGroupId,
                recipeId = null
            )

            if (existing == null) {
                shoppingListItemGroupDao.insert(
                    ShoppingListItemGroup(
                        shoppingListId = shoppingListId,
                        itemGroupId = itemGroupId,
                        recipeId = null,
                        quantity = quantity,
                        isChecked = false
                    )
                )
            } else {
                shoppingListItemGroupDao.update(
                    existing.copy(quantity = existing.quantity + quantity)
                )
            }
        }
    }

    // UI helpers using ShoppingListEntryUi
    // --------------------------------------------------------------------------------

    fun delete(entry: ShoppingListEntryUi) {
        viewModelScope.launch {
            shoppingListItemGroupDao.deleteItem(
                shoppingListId = shoppingListId,
                itemGroupId = entry.itemGroupId,
                recipeId = entry.recipeId
            )
        }
    }

    fun setChecked(entry: ShoppingListEntryUi, checked: Boolean) {
        viewModelScope.launch {
            shoppingListItemGroupDao.updateCheckmark(
                shoppingListId = shoppingListId,
                itemGroupId = entry.itemGroupId,
                recipeId = entry.recipeId,
                checked = checked
            )
        }
    }
}
