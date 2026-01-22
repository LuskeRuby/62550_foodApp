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
        _selectedStores.update { current ->
            if (current.contains(storeId)) current - storeId else current + storeId
        }
    }

    /** Select all stores from a given set of IDs */
    fun selectAll(storeIds: Set<Long>) {
        _selectedStores.value = storeIds
    }

    /** Clear all selected stores */
    fun clear() {
        _selectedStores.value = emptySet()
    }
}