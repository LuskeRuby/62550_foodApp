package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ItemDao
import com.example.a62550_foodapp.db.projection.ItemWithPriceAndCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ItemViewModel(
    private val itemDao: ItemDao
): ViewModel() {

    val items: StateFlow<List<ItemWithPriceAndCategory>> =
        itemDao.
        getAllItemWithPriceAndCategory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

}