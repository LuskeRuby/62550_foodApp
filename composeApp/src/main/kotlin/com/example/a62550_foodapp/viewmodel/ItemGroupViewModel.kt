package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.ItemGroupDao
import com.example.a62550_foodapp.db.entity.ItemGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ItemGroupViewModel(
    itemGroupDao: ItemGroupDao
): ViewModel() {
    val itemGroups: StateFlow<List<ItemGroup>> =
        itemGroupDao.getAllItemGroups()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}