package com.example.a62550_foodapp.di

import androidx.compose.runtime.Composable
import com.example.a62550_foodapp.viewmodel.ShoppingListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun getShoppingListViewModel(): ShoppingListViewModel {
    return koinViewModel<ShoppingListViewModel>()
}
