package com.example.a62550_foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.a62550_foodapp.ui.MainView
import com.example.a62550_foodapp.ui.recipe.RecipePage
import com.example.a62550_foodapp.ui.recipe.CreateRecipeScreen
import com.example.a62550_foodapp.ui.recipe.RecipeDetailScreen
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.lifecycleScope
import com.example.a62550_foodapp.NavState.*
import com.example.a62550_foodapp.db.AppDatabase
import com.example.a62550_foodapp.db.DatabaseMockData
import com.example.a62550_foodapp.ui.discover.DiscoverRecipesScreen
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (BuildConfig.DEBUG) {
            val database: AppDatabase = get()

            lifecycleScope.launch {
                DatabaseMockData.populate(context = this@MainActivity, database = database)
            }
        }

        setContent {
            var navigationState by remember { mutableStateOf<NavState>(NavState.RecipeList) }
            val recipeViewModel: RecipeViewModel = koinViewModel()

            MainView(
                recipeContent = {
                    when (val state = navigationState) {
                        is NavState.RecipeList -> {
                            RecipePage(
                                recipeViewModel = recipeViewModel,
                                onAddRecipeClick = {
                                    navigationState = CreateRecipe(null)
                                },
                                onRecipeClick = { id ->
                                    navigationState = RecipeDetail(id)
                                },
                                onDiscoverRecipesClick = {
                                    navigationState = NavState.DiscoverRecipes
                                }
                            )
                        }
                        is NavState.CreateRecipe -> {
                            BackHandler {
                                navigationState = NavState.RecipeList
                            }
                            CreateRecipeScreen(
                                recipeViewModel = recipeViewModel,
                                existingRecipeId = state.existingRecipeId,
                                onRecipeSaved = {
                                    navigationState = NavState.RecipeList
                                }
                            )
                        }
                        is NavState.RecipeDetail -> {
                            BackHandler {
                                navigationState = NavState.RecipeList
                            }
                            RecipeDetailScreen(
                                recipeId = state.id,
                                recipeViewModel = recipeViewModel,
                                onBack = {
                                    navigationState = NavState.RecipeList
                                },
                                onEdit = { id ->
                                    navigationState = CreateRecipe(id)
                                }
                            )
                        }
                        is NavState.DiscoverRecipes -> {
                            BackHandler {
                                navigationState = NavState.RecipeList
                            }

                            DiscoverRecipesScreen(
                                onBack = {
                                    navigationState = NavState.RecipeList
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}

sealed class NavState {
    object RecipeList : NavState()
    object DiscoverRecipes : NavState()
    data class CreateRecipe(val existingRecipeId: Int? = null) : NavState()
    data class RecipeDetail(val id: Int) : NavState()
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
