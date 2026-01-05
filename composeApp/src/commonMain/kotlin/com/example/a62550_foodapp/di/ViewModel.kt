package com.example.a62550_foodapp.di

import androidx.compose.runtime.Composable
import com.example.a62550_foodapp.viewmodel.MainViewModel

@Composable
expect fun getViewModel(): MainViewModel
