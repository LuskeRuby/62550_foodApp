@file:JvmName("AndroidAppModuleKt")
package com.example.a62550_foodapp.di

import com.example.a62550_foodapp.viewmodel.AndroidMainViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.*

val androidModule = module {
    viewModel { AndroidMainViewModel() }
}
