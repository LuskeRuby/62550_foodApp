package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ItemDao
import com.example.a62550_foodapp.db.entity.Item
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ItemViewModel(
    private val itemDao: ItemDao
): ViewModel() {

    val items: StateFlow<List<Item>> = itemDao.getAll()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

}