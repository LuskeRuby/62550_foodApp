package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListItemGroupDao
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.db.entity.ShoppingListItemGroup
import com.example.a62550_foodapp.db.projection.ShoppingListEntry
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

class ShoppingListDetailsViewModel(
    private val shoppingListId: Long,
    private val shoppingListItemGroupDao: ShoppingListItemGroupDao,
    private val storeFilterViewModel: StoreFilterViewModel
) : ViewModel() {


    // --------------------------------------------------------------------------------
    // Shopping List Item Groups (raw logical ingredients)
    // --------------------------------------------------------------------------------
    private val itemGroupList: StateFlow<List<ShoppingListItemGroup>> =
        shoppingListItemGroupDao.getItemGroupsMatchingListId(shoppingListId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // --------------------------------------------------------------------------------
    // ADD ITEM GROUP
    // --------------------------------------------------------------------------------
    // TODO handle duplicate items
    fun addItemGroup(addedItem: ItemGroup, portionQuantity: Int?, portionSize: Float) {
        viewModelScope.launch {

        val newEntry = ShoppingListItemGroup(
            shoppingListId = shoppingListId,
            itemGroupId = addedItem.id,
            recipeId = null,
            portionQuantity = portionQuantity ?: 1,
            portionSize = portionSize,
            isChecked = false
        )

            val existInList = itemGroupList.value.any {
                it.itemGroupId == newEntry.itemGroupId &&
                        it.recipeId == newEntry.recipeId
            }

        try {
            if (existInList) {
                updateItemGroup(newEntry)
            } else {
                shoppingListItemGroupDao.insert(newEntry)
            }

        } catch (e: Exception) {
            // Handle exception (e.g., log it)
            e.printStackTrace()
        }
    }
}


    // --------------------------------------------------------------------------------
    // UPDATE ITEM GROUP (ENTITY-BASED, NOT PROJECTION)
    // --------------------------------------------------------------------------------
    fun updateItemGroup(itemToUpdate: ShoppingListItemGroup) {
        viewModelScope.launch {

            val previousPortionQuantity: Int = itemGroupList.value.first {
                it.id == itemToUpdate.id
            }.portionQuantity

            val updateEntry =
                ShoppingListItemGroup(
                    id = itemToUpdate.id,
                    shoppingListId = itemToUpdate.shoppingListId,
                    itemGroupId = itemToUpdate.itemGroupId,
                    recipeId = itemToUpdate.recipeId,
                    portionQuantity = itemToUpdate.portionQuantity + previousPortionQuantity,
                    portionSize = itemToUpdate.portionSize,
                    isChecked = itemToUpdate.isChecked
                )

            shoppingListItemGroupDao.update(updateEntry)
        }
    }

    // --------------------------------------------------------------------------------
    // DELETE
    // --------------------------------------------------------------------------------
    fun deleteItemGroup(item: ShoppingListItemGroup) {
        viewModelScope.launch {
            shoppingListItemGroupDao.delete(
                shoppingListId = item.shoppingListId,
                itemGroupId = item.itemGroupId,
                recipeId = item.recipeId
            )
        }
    }

    // --------------------------------------------------------------------------------
    // CHECKMARK
    // --------------------------------------------------------------------------------
    fun setCheckedItemGroup(
        itemGroupId: Long,
        recipeId: Long?,
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

    // --------------------------------------------------------------------------------
    // STORE FILTER (same pattern as RecipePage)
    // --------------------------------------------------------------------------------
    @OptIn(ExperimentalCoroutinesApi::class)
    val shoppingListEntries: StateFlow<List<ShoppingListEntry>> =
        storeFilterViewModel.selectedStores
            .flatMapLatest { stores ->
                shoppingListItemGroupDao.getCheapestShoppingListEntriesFlow(
                    shoppingListId = shoppingListId,
                    storeIds = stores.toList(),
                    storeCount = stores.size
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // --------------------------------------------------------------------------------
    // CHEAPEST SHOPPING LIST ENTRIES (PROJECTION FOR UI)
    // --------------------------------------------------------------------------------
    @OptIn(ExperimentalCoroutinesApi::class)
    val shoppingListEntries: StateFlow<List<ShoppingListEntry>> =
        selectedStores
            .flatMapLatest { stores ->
                shoppingListItemGroupDao.getCheapestShoppingListEntriesFlow(
                    shoppingListId = shoppingListId,
                    storeIds = stores.toList(),
                    storeCount = stores.size
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )


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
    val items: StateFlow<List<ShoppingListEntry>> =
        selectedSupermarketId
            .flatMapLatest { supermarketId ->
                shoppingListItemGroupDao.getShoppingListEntriesByStoreFlow(
                    shoppingListId = shoppingListId,
                    supermarketId = supermarketId
                )
            }
            .map { entries ->
                entries.map {
                    ShoppingListEntry(
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
                        portionQuantity = quantity,
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
