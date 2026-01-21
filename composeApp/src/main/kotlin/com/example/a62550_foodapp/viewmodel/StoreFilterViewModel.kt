package com.example.a62550_foodapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class StoreFilterViewModel : ViewModel() {

    // Backing state: selected supermarket IDs
    private val _selectedStores = MutableStateFlow<Set<Long>>(emptySet())

    // Public immutable state
    val selectedStores: StateFlow<Set<Long>> = _selectedStores

    /** Replace entire selection */
    fun setSelectedStores(stores: Set<Long>) {
        _selectedStores.value = stores
    }

    /** Toggle a store on/off in the filter */
    fun toggleStore(storeId: Long) {
        _selectedStores.value =
            if (_selectedStores.value.contains(storeId))
                _selectedStores.value - storeId
            else
                _selectedStores.value + storeId
    }

    /** Clear all selected stores (means: use all stores) */
    fun clear() {
        _selectedStores.value = emptySet()
    }
}