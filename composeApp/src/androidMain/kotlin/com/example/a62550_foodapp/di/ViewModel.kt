package com.example.a62550_foodapp.di

import androidx.compose.runtime.Composable
import com.example.a62550_foodapp.viewmodel.AndroidMainViewModel
import com.example.a62550_foodapp.viewmodel.AndroidShoppingListDetailsViewModel
import com.example.a62550_foodapp.viewmodel.MainViewModel
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun getViewModel(): MainViewModel {
    return koinViewModel<AndroidMainViewModel>()
}

@Composable
fun getDetailsViewModel(): ShoppingListDetailsViewModel {
    return koinViewModel<AndroidShoppingListDetailsViewModel>()
}
