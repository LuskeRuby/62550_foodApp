package com.example.a62550_foodapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ItemGroupDao
import com.example.a62550_foodapp.db.dao.ShoppingListItemGroupDao
import com.example.a62550_foodapp.db.dao.SupermarketDao
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.db.entity.ShoppingListItemGroup
import com.example.a62550_foodapp.db.projection.ShoppingListEntry
import com.example.a62550_foodapp.db.projection.ShoppingListItemGroupEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShoppingListDetailsViewModel(
    private val shoppingListId: Long,
    private val shoppingListItemGroupDao: ShoppingListItemGroupDao,
    private val supermarketDao: SupermarketDao,
    private val itemGroupDao: ItemGroupDao
) : ViewModel() {

    private val storeFilter = MutableStateFlow<Set<Long>>(emptySet())

    fun setStoreFilter(stores: Set<Long>) {
        storeFilter.value = stores
    }
    private val storeNameMap: StateFlow<Map<Long, String>> =
        supermarketDao.getAll()
            .map { list -> list.associate { it.id to it.name } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )

    // actual DB table we edit
    private val itemGroupList: StateFlow<List<ShoppingListItemGroup>> =
        shoppingListItemGroupDao.getItemGroupsMatchingListId(shoppingListId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // items visible in ShoppingListDetails
    @OptIn(ExperimentalCoroutinesApi::class)
    val items: StateFlow<List<ShoppingListEntry>> =
        storeFilter.flatMapLatest { selectedStores ->
            shoppingListItemGroupDao
                .getShoppingListEntriesWithFallbackFlow(
                    shoppingListId = shoppingListId,
                    storeIds = selectedStores.toList(),
                    storeCount = selectedStores.size
                )
                .map { rawEntries ->

                    val available = rawEntries.filter { it.price != null }

                    // Global, store-agnostic bucket
                    val unavailable = rawEntries
                        .filter { it.price == null }
                        .map {
                            it.copy(
                                superMarketName = null,
                                superMarketLogo = null,
                                category = "Utilgængelige varer"
                            )
                        }

                    available + unavailable
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // for shoppingListDetails searchbar suggestions
    val itemGroups: StateFlow<List<ItemGroup>> =
        itemGroupDao.getAllItemGroups()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // items visible while adding itemGroups to shopping list
    val itemGroupEntries: StateFlow<List<ShoppingListItemGroupEntry>> =
        shoppingListItemGroupDao.getAllItemGroupEntries(shoppingListId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )


    // Total
    data class TotalUi(
        val total: Float,
        val missingCount: Int
    )

    val shoppingListTotalPrice: StateFlow<TotalUi> =
        items.map { list ->
            TotalUi(
                total = list.sumOf { ((it.price ?: 0f) * it.quantity).toDouble() }.toFloat(),
                missingCount = list.count { it.price == null }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TotalUi(0f, 0)
        )

    fun add(addedItem: ItemGroup, recipeId: Long? = null, portionQuantity: Int = 1, portionSize: Float) {
        viewModelScope.launch {
            val entry = ShoppingListItemGroup(
                shoppingListId = shoppingListId,
                itemGroupId = addedItem.id,
                recipeId = recipeId,
                portionQuantity = portionQuantity,
                portionSize = portionSize,
                isChecked = false
            )

            val doesEntryExistInDB = shoppingListItemGroupDao.existsInDB(
                shoppingListId = entry.shoppingListId,
                itemGroupId = entry.itemGroupId,
                recipeId = entry.recipeId
            )

            if (doesEntryExistInDB) {
                update(entry)
            } else {
                try {
                    shoppingListItemGroupDao.insert(entry)
                } catch (e: Exception) {
                    Log.e("ShoppingListDetailsVM", "Failed to insert ShoppingListItemGroup", e)
                }
            }
        }
    }

    fun update(item: ShoppingListItemGroup) {
        viewModelScope.launch {

            val previousQuantity = shoppingListItemGroupDao.getQuantity(
                shoppingListId = item.shoppingListId,
                itemGroupId = item.itemGroupId,
                recipeId = item.recipeId
            )

            try {
                shoppingListItemGroupDao.update(
                    shoppingListId = item.shoppingListId,
                    itemGroupId = item.itemGroupId,
                    recipeId = item.recipeId,
                    portionQuantity = item.portionQuantity + previousQuantity,
                    portionSize = item.portionSize,
                    isChecked = item.isChecked
                )
            } catch (e: Exception) {
                Log.e("ShoppingListDetailsVM", "Failed to update ShoppingListItemGroup", e)
            }

        }
    }

    fun delete(item: ShoppingListEntry) {
        viewModelScope.launch {
            try {
                shoppingListItemGroupDao.delete(id = item.id)
            } catch (e: Exception) {
                Log.e("ShoppingListDetailsVM", "Failed to delete ShoppingListItemGroup", e)
            }

        }
    }

    fun delete(item: ShoppingListItemGroupEntry) {
        viewModelScope.launch {
            try {
                shoppingListItemGroupDao.delete(id = item.id)
            } catch (e: Exception) {
                Log.e("ShoppingListDetailsVM", "Failed to delete ShoppingListItemGroup", e)
            }
        }
    }

    fun setCheckmark(item: ShoppingListEntry, checked: Boolean) {
        viewModelScope.launch {
            try {
                shoppingListItemGroupDao.updateCheckmark(
                    shoppingListId = shoppingListId,
                    itemGroupId = item.itemGroupId,
                    recipeId = item.recipeId,
                    checked = checked
                )
            } catch (e: Exception) {
                Log.e("ShoppingListDetailsVM", "Failed to set checkmark ShoppingListItemGroup", e)
            }
        }
    }
}
