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

    private val itemGroupList: StateFlow<List<ShoppingListItemGroup>> =
        shoppingListItemGroupDao.getItemGroupsMatchingListId(shoppingListId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

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

    val itemGroups: StateFlow<List<ItemGroup>> =
        itemGroupDao.getAllItemGroups()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

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

    fun add(addedItem: ItemGroup, portionQuantity: Int = 1, portionSize: Float) {
        viewModelScope.launch {
            val entry = ShoppingListItemGroup(
                shoppingListId = shoppingListId,
                itemGroupId = addedItem.id,
                recipeId = null,
                portionQuantity = portionQuantity,
                portionSize = portionSize,
                isChecked = false
            )

            if (itemGroupList.value.any { it == entry }) {
                update(entry)
            } else {
                shoppingListItemGroupDao.insert(entry)
            }
        }
    }

    fun update(item: ShoppingListItemGroup) {
        viewModelScope.launch {
            val prevQty = itemGroupList.value.first { it.id == item.id }.portionQuantity
            shoppingListItemGroupDao.update(
                item.copy(portionQuantity = item.portionQuantity + prevQty)
            )
        }
    }

    fun delete(item: ShoppingListEntry) {
        viewModelScope.launch {
            shoppingListItemGroupDao.delete(id = item.id)
        }
    }

    fun delete(item: ShoppingListItemGroupEntry) {
        viewModelScope.launch {
            shoppingListItemGroupDao.delete(id = item.id)
        }
    }

    fun setCheckmark(item: ShoppingListEntry, checked: Boolean) {
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
