package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StoreFilterViewModel : ViewModel() {
    private val _selectedStores = MutableStateFlow<Set<Int>>(emptySet())
    val selectedStores: StateFlow<Set<Int>> = _selectedStores

    fun setSelectedStores(stores: Set<Int>) {
        _selectedStores.value = stores
    }
}