package com.example.a62550_foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.a62550_foodapp.ui.MainView
import com.example.a62550_foodapp.ui.RecipePage
import com.example.a62550_foodapp.ui.recipe.CreateRecipeScreen
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            var showCreateRecipe by remember { mutableStateOf(false) }
            val recipeViewModel: RecipeViewModel = koinViewModel()
            val recipes by recipeViewModel.recipes.collectAsState()

            MainView(
                recipeContent = {
                    if (showCreateRecipe) {
                        BackHandler {
                            showCreateRecipe = false
                        }
                        CreateRecipeScreen(
                            recipeViewModel = recipeViewModel,
                            onRecipeSaved = {
                                showCreateRecipe = false
                            }
                        )
                    } else {
                        RecipePage(
                            recipes = recipes,
                            onAddRecipeClick = {
                                showCreateRecipe = true
                            }
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
