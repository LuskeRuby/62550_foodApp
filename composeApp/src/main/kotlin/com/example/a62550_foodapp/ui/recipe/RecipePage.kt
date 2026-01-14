package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.model.Recipe
import com.example.a62550_foodapp.ui.components.LocalImage
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecipePage(
    recipes: List<Recipe>,
    onAddRecipeClick: () -> Unit,
    onDiscoverRecipesClick: () -> Unit,
    onRecipeClick: (Int) -> Unit,
    recipeViewModel: RecipeViewModel = koinViewModel(),
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    var sortedRecipes by remember { mutableStateOf<List<Pair<Recipe, Float>>>(emptyList()) }

    LaunchedEffect(recipes) {
        if (recipes.isNotEmpty()) {
            // Load prices for all recipes and sort by price (cheapest first)
            val recipesWithPrices = recipes.map { recipe ->
                val price = recipeViewModel.getRecipePrice(recipe.id)
                recipe to price
            }.sortedBy { it.second }
            sortedRecipes = recipesWithPrices
        } else {
            sortedRecipes = emptyList()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
    ) {
        if (sortedRecipes.isEmpty()) {
            Text(
                text = "No recipes yet. Click + to add one!",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
                color = themeViewModel.textSecondary
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sortedRecipes) { (recipe, price) ->
                    RecipeCard(
                        recipe = recipe,
                        price = price,
                        themeViewModel = themeViewModel,
                        onClick = { onRecipeClick(recipe.id) }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = onDiscoverRecipesClick,
                containerColor = themeViewModel.secondaryColor,
                contentColor = themeViewModel.onSecondaryColor,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Discover recipes"
                    )
                },
                text = {
                    Text(
                        text = "Find opskrifter på engelsk",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )

            FloatingActionButton(
                onClick = onAddRecipeClick,
                shape = CircleShape,
                containerColor = themeViewModel.addButtonColor,
                contentColor = themeViewModel.onPrimaryColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Recipe")
            }
        }
    }
}


@Composable
fun RecipeCard(
    recipe: Recipe,
    price: Float = 0f,
    themeViewModel: ThemeViewModel,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = themeViewModel.surfaceColor),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // IMAGE + PRICE BADGE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .background(themeViewModel.secondaryColor)
            ) {
                LocalImage(
                    imagePath = recipe.imagePath,
                    modifier = Modifier.fillMaxSize()
                )

                // PRICE BADGE
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = themeViewModel.priceTagColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = String.format("%.2f kr", price),
                        color = themeViewModel.onPrimaryColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // TEXT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = themeViewModel.textPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                recipe.description?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = themeViewModel.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
