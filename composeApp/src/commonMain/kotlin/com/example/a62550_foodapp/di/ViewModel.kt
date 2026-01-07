package com.example.a62550_foodapp.di

import androidx.compose.runtime.Composable
import com.example.a62550_foodapp.viewmodel.MainViewModel
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel

@Composable
expect fun getViewModel(): MainViewModel

@Composable
expect fun getDetailsViewModel(): ShoppingListDetailsViewModel
