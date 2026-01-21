package com.example.a62550_foodapp.viewmodel

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.Int
import kotlin.collections.associate

class ShoppingListDetailsViewModel(
    private val shoppingListId: Long,
    private val shoppingListItemGroupDao: ShoppingListItemGroupDao,
    private val supermarketDao: SupermarketDao
    private val itemGroupDao: ItemGroupDao,
    private val shoppingListItemGroupDao: ShoppingListItemGroupDao
) : ViewModel() {

    private val storeFilter = MutableStateFlow<Set<Long>>(emptySet())

    fun setStoreFilter(stores: Set<Long>) {
        storeFilter.value = stores

    }
    private val storeNameMap: StateFlow<Map<Long, String>> =
        supermarketDao
            .getAll()
            .map { supermarkets ->
                supermarkets.associate { it.id to it.name }
            }
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

                    //Unavailable items (no price anywhere)
                    val unavailable = rawEntries
                        .filter { it.price == null }
                        .map {
                            it.copy(
                                superMarketName = null, // 🚨 important
                                category = "Utilgængelige varer"
                            )
                        }

                    // 3️⃣ Final list
                    available + unavailable
                }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // for shoppingListDetails searchbar
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


    private fun storeNameResolver(storeId: Long): String? =
        storeNameMap.value[storeId]

    // Total
    data class TotalUi(
        val total: Float,
        val missingCount: Int
    )

    val shoppingListTotalPrice: StateFlow<TotalUi> =
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


    fun add(addedItem: ItemGroup, portionQuantity: Int = 1, portionSize: Float) {
        viewModelScope.launch {

            val newEntry = ShoppingListItemGroup(
                shoppingListId = shoppingListId,
                itemGroupId = addedItem.id,
                recipeId = null,
                portionQuantity = portionQuantity,
                portionSize = portionSize,
                isChecked = false
            )

            val doesNewEntryExistInList = itemGroupList.value.any {
                it.equals(newEntry)
            }

            if (doesNewEntryExistInList) {
                update(newEntry)
            } else {
                try {
                    shoppingListItemGroupDao.insert(newEntry)
                } catch (e: Exception) {
                    // Handle exception (e.g., log it)
                    e.printStackTrace()
                }
            }

        }
    }


    fun update(itemToUpdate: ShoppingListItemGroup) {
        viewModelScope.launch {

            val previousPortionQuantity: Int = itemGroupList.value.first {
                it.id == itemToUpdate.id
            }.portionQuantity

            val updateEntry = itemToUpdate.copy(
                portionQuantity = itemToUpdate.portionQuantity + previousPortionQuantity
            )

            try {
                shoppingListItemGroupDao.update(updateEntry)
            } catch (e: Exception) {
                // Handle exception (e.g., log it)
                e.printStackTrace()
            }
        }
    }


    fun delete(item: ShoppingListEntry) {
        viewModelScope.launch {
            shoppingListItemGroupDao.delete( id = item.id )
        }
    }

    fun delete(item: ShoppingListItemGroupEntry) {
        viewModelScope.launch {
            shoppingListItemGroupDao.delete( id = item.id )
        }
    }


    fun setCheckmark(
        item: ShoppingListEntry,
        checked: Boolean
    ) {
        viewModelScope.launch {
            shoppingListItemGroupDao.updateCheckmark(
                shoppingListId = shoppingListId,
                itemGroupId = item.itemGroupId,
                recipeId = item.recipeId,
                checked = checked
            )
        }
    }
}

        fun setCheckmark(
            item: ShoppingListEntry,
            checked: Boolean
        ) {
            viewModelScope.launch {
                shoppingListItemGroupDao.updateCheckmark(
                    shoppingListId = shoppingListId,
                    itemGroupId = item.itemGroupId,
                    recipeId = item.recipeId,
                    checked = checked
                )
            }
        }
}