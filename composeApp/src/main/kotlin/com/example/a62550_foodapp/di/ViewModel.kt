package com.example.a62550_foodapp.di

import androidx.compose.runtime.Composable
import com.example.a62550_foodapp.viewmodel.MainViewModel
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import com.example.a62550_foodapp.viewmodel.ShoppingListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun getViewModel(): MainViewModel {
    return koinViewModel<MainViewModel>()
}

@Composable
fun getDetailsViewModel(): ShoppingListDetailsViewModel {
    return koinViewModel<ShoppingListDetailsViewModel>()
}

@Composable
fun getShoppingListViewModel(): ShoppingListViewModel {
    return koinViewModel<ShoppingListViewModel>()
}
