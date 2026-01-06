package com.example.a62550_foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import com.example.a62550_foodapp.ui.recipe.CreateRecipeScreen
import androidx.compose.material3.MaterialTheme
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val recipeViewModel: RecipeViewModel = koinViewModel()
                CreateRecipeScreen(recipeViewModel)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}