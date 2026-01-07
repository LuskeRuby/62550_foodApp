package com.example.a62550_foodapp.viewmodel

import com.example.a62550_foodapp.model.ShoppingListRowDisplay
import kotlinx.coroutines.flow.StateFlow

interface ShoppingListDetailsViewModel {
    val items: StateFlow<List<ShoppingListRowDisplay>>
}
