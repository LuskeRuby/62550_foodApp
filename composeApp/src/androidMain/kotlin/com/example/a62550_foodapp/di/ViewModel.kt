package com.example.a62550_foodapp.di

import androidx.compose.runtime.Composable
import com.example.a62550_foodapp.viewmodel.AndroidMainViewModel
import com.example.a62550_foodapp.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
actual fun getViewModel(): MainViewModel {
    return koinViewModel<AndroidMainViewModel>()
}
