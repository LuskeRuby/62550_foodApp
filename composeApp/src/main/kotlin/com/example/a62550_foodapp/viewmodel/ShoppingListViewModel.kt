package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ShoppingListDao
import com.example.a62550_foodapp.db.entity.ShoppingList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel(
    private val shoppingListDao: ShoppingListDao
) : ViewModel() {

    val shoppingLists: StateFlow<List<ShoppingList>> =
        shoppingListDao.getAll()
            .map { shoppingList ->
                shoppingList.map {
                    ShoppingList(
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
                ShoppingList(
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
                ShoppingList(
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
                ShoppingList(
                    id = id,
                    name = name
                )
            )
        }
    }

}