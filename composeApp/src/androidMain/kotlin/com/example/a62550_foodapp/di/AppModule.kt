package com.example.a62550_foodapp.di

import com.example.a62550_foodapp.viewmodel.AndroidMainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AndroidMainViewModel() }
    //TODO fix deprecated androidViewModel
}
