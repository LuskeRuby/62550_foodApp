package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListItemDao
import com.example.a62550_foodapp.db.projection.ShoppingListSupermarketRow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListDetailsViewModel(
    private val shoppingListItemDao: ShoppingListItemDao
) : ViewModel() {

    val uiState: StateFlow<List<SupermarketUi>> =
        shoppingListItemDao
            .getShoppingListBySupermarket(
                year = 2024,
                week = 28
            )
            .map { rows -> rows.toUi() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun onCheckedChange(itemId: Int, checked: Boolean) {
        viewModelScope.launch {
            shoppingListItemDao.updateChecked(
                itemId = itemId,
                checked = checked
            )
        }
    }
}


data class SupermarketUi(
    val id: Int,
    val name: String,
    val categories: List<CategoryUi>,
    val totalPrice: Float
)

data class CategoryUi(
    val groupId: Int?,
    val items: List<ItemUi>
)

data class ItemUi(
    val itemId: Int,
    val name: String,
    val quantity: Float,
    val unit: String?,
    val price: Float,
    val isChecked: Boolean
)

private fun List<ShoppingListSupermarketRow>.toUi(): List<SupermarketUi> =
    groupBy { it.supermarketId }
        .map { (_, supermarketRows) ->

            val categories =
                supermarketRows
                    .groupBy { it.itemGroupId }
                    .map { (groupId, groupRows) ->
                        CategoryUi(
                            groupId = groupId,
                            items = groupRows.map {
                                ItemUi(
                                    itemId = it.itemId,
                                    name = it.itemName,
                                    quantity = it.quantity,
                                    unit = it.unit,
                                    price = it.price,
                                    isChecked = it.isChecked
                                )
                            }
                        )
                    }

            SupermarketUi(
                id = supermarketRows.first().supermarketId,
                name = supermarketRows.first().supermarketName,
                categories = categories,
                totalPrice = supermarketRows.sumOf { it.price.toDouble() }.toFloat()

            )
        }
