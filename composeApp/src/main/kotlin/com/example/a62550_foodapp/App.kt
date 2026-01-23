package com.example.a62550_foodapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.a62550_foodapp.ui.MainView

@Composable
@Preview(showBackground = true)
fun App() {
    MaterialTheme {
        MainView(
            recipeContent = {
            }
        )
    }
}
