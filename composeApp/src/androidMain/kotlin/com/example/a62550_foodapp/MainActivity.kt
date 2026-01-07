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
import androidx.lifecycle.lifecycleScope
import com.example.a62550_foodapp.db.AppDatabase
import com.example.a62550_foodapp.db.DatabaseSeeder
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (BuildConfig.DEBUG) {
            val database: AppDatabase = get()

            lifecycleScope.launch {
                DatabaseSeeder.seed(database)
            }
        }

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
