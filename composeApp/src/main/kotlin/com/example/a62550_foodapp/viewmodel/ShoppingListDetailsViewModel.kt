package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListItemGroupDao
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.db.entity.ShoppingListItemGroup
import com.example.a62550_foodapp.db.projection.ShoppingListEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListDetailsViewModel(
    private val shoppingListId: Long,
    private val shoppingListItemGroupDao: ShoppingListItemGroupDao,
    private val storeFilterViewModel: StoreFilterViewModel
) : ViewModel() {


    // RAW SHOPPING LIST CONTENT (item_group based)
    private val itemGroupList: StateFlow<List<ShoppingListItemGroup>> =
        shoppingListItemGroupDao.getItemGroupsMatchingListId(shoppingListId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val items: StateFlow<List<ShoppingListEntry>> =
        shoppingListItemGroupDao.getCheapestShoppingListEntriesFlow(
            shoppingListId = shoppingListId,
            storeIds = storeFilterViewModel.selectedStores.value.toList(),
            storeCount = storeFilterViewModel.selectedStores.value.size
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // TOTAL PRICE
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


    // ADD / UPDATE
    fun add(
        addedItem: ItemGroup,
        portionQuantity: Int = 1,
        portionSize: Float
    ) {
        viewModelScope.launch {

            val existing = itemGroupList.value.firstOrNull {
                it.itemGroupId == addedItem.id &&
                        it.recipeId == null
            }

            if (existing != null) {
                shoppingListItemGroupDao.update(
                    existing.copy(
                        portionQuantity = existing.portionQuantity + portionQuantity
                    )
                )
            } else {
                shoppingListItemGroupDao.insert(
                    ShoppingListItemGroup(
                        shoppingListId = shoppingListId,
                        itemGroupId = addedItem.id,
                        recipeId = null,
                        portionQuantity = portionQuantity,
                        portionSize = portionSize,
                        isChecked = false
                    )
                )
            }
        }
    }

    // DELETE
    fun delete(item: ShoppingListEntry) {
        viewModelScope.launch {
            shoppingListItemGroupDao.delete(
                shoppingListId = shoppingListId,
                itemGroupId = item.itemGroupId,
                recipeId = item.recipeId
            )
        }
    }


    // CHECKMARK
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
