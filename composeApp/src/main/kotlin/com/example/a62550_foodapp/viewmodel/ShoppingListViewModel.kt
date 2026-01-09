package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListDao
import com.example.a62550_foodapp.db.entity.ShoppingList as ShoppingListEntity
import com.example.a62550_foodapp.model.ShoppingList as ShoppingListModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val shoppingListDao: ShoppingListDao
) : ViewModel() {

    val shoppingLists: StateFlow<List<ShoppingListModel>> =
        shoppingListDao.getAll()
            .map { entity ->
                entity.map {
                    ShoppingListModel(
                        id = it.id,
                        name = it.name,
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun createShoppingList(
        name: String
    ) {
        viewModelScope.launch {
            val shoppingListId = shoppingListDao.insert(
                ShoppingListEntity(
                    id = 0,
                    name = name
                )
            ).toInt()
        }
    }

    fun editShoppingList(
        id: Int,
        name: String
    ) {
        viewModelScope.launch {
            shoppingListDao.update(
                ShoppingListEntity(
                    id = id,
                    name = name
                )
            )
        }
    }

    fun deleteShoppingList(
        id: Int,
        name: String
    ) {
        viewModelScope.launch {
            shoppingListDao.delete(
                ShoppingListEntity(
                    id = id,
                    name = name
                )
            )
        }
    }

}