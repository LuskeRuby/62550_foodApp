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
import com.example.a62550_foodapp.ui.recipe.RecipeDetailScreen
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.lifecycleScope
import com.example.a62550_foodapp.NavState.*
import com.example.a62550_foodapp.db.AppDatabase
import com.example.a62550_foodapp.db.DatabaseMockData
import com.example.a62550_foodapp.ui.discover.DiscoverRecipesScreen
import com.example.a62550_foodapp.ui.recipe.ApiRecipeDetailScreen
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import com.example.a62550_foodapp.ui.recipe.CreateRecipeScreen

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
            var navigationState by remember { mutableStateOf<NavState>(RecipeList) }
            val recipeViewModel: RecipeViewModel = koinViewModel()

            MainView(
                recipeContent = {
                    when (val state = navigationState) {

                        // ---------- LIST ----------
                        is RecipeList -> {
                            RecipePage(
                                recipeViewModel = recipeViewModel,
                                onAddRecipeClick = {
                                    navigationState = CreateRecipe(
                                        existingRecipeId = null,
                                        returnTo = RecipeList
                                    )
                                },
                                onRecipeClick = { id ->
                                    navigationState = RecipeDetail(id)
                                },
                                onDiscoverRecipesClick = {
                                    navigationState = DiscoverRecipes
                                }
                            )
                        }

                        // ---------- CREATE / EDIT ----------
                        is CreateRecipe -> {
                            BackHandler {
                                navigationState = state.returnTo
                            }

                            CreateRecipeScreen(
                                recipeViewModel = recipeViewModel,
                                existingRecipeId = state.existingRecipeId,
                                onRecipeSaved = {
                                    navigationState = state.returnTo
                                },
                                onBack = {
                                    navigationState = state.returnTo
                                }
                            )
                        }

                        // ---------- DETAIL ----------
                        is RecipeDetail -> {
                            BackHandler {
                                navigationState = RecipeList
                            }

                            RecipeDetailScreen(
                                recipeId = state.id,
                                recipeViewModel = recipeViewModel,
                                onBack = {
                                    navigationState = RecipeList
                                },
                                onEdit = { id ->
                                    navigationState = CreateRecipe(
                                        existingRecipeId = id,
                                        returnTo = RecipeDetail(id)
                                    )
                                }
                            )
                        }

                        // ---------- DISCOVER ----------
                        is DiscoverRecipes -> {
                            BackHandler {
                                navigationState = RecipeList
                            }

                            DiscoverRecipesScreen(
                                onBack = {
                                    navigationState = RecipeList
                                },
                                onMealClick = { mealId ->
                                    navigationState = NavState.ApiRecipeDetail(mealId)
                                }
                            )
                        }

                        // ---------- API DETAIL ----------
                        is NavState.ApiRecipeDetail -> {
                            BackHandler {
                                navigationState = DiscoverRecipes
                            }

                            ApiRecipeDetailScreen(
                                mealId = state.mealId,
                                onBack = {
                                    navigationState = DiscoverRecipes
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
    data class CreateRecipe(
        val existingRecipeId: Long? = null,
        val returnTo: NavState = RecipeList
    ) : NavState()
    data class RecipeDetail(val id: Long) : NavState()

    data class ApiRecipeDetail(val mealId: String) : NavState()
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
