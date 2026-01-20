package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.filled.Public
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel

@Composable
fun RecipePage(
    onAddRecipeClick: () -> Unit,
    onDiscoverRecipesClick: () -> Unit, // kept for nav, not used here
    onRecipeClick: (Long) -> Unit,
    recipeViewModel: RecipeViewModel = koinViewModel(),
    themeViewModel: ThemeViewModel = koinViewModel()
) {

    // --- DATA ---
    val filterViewModel: StoreFilterViewModel = koinViewModel()
    val selectedStores by filterViewModel.selectedStores.collectAsState()

    val allRecipes by recipeViewModel
        .getRecipesWithPricesFlow(selectedStores)
        .collectAsState(initial = emptyList())

    // --- UI STATE ---
    var searchText by remember { mutableStateOf("") }

    val visibleRecipes = remember(allRecipes, searchText) {
        allRecipes.filter { (recipe, _) ->
            recipe.title.contains(searchText, ignoreCase = true)
        }
    }


    val gridState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ===== FIXED SEARCH BAR =====
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Søg opskrift") },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            )

            // ===== GRID =====
            if (visibleRecipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "Ingen opskrifter fundet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = themeViewModel.textSecondary
                    )
                }
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 96.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(visibleRecipes) { (recipe, price) ->
                        RecipeCard(
                            recipe = recipe,
                            basePrice = price,
                            recipeViewModel = recipeViewModel,
                            themeViewModel = themeViewModel,
                            selectedStores = selectedStores,
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }

        // ===== ADD RECIPE FAB =====
        FloatingActionButton(
            onClick = onAddRecipeClick,
            containerColor = themeViewModel.addButtonColor,
            contentColor = themeViewModel.onPrimaryColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Recipe")
        }

        // ===== DISCOVER FAB (LEFT) =====
        FloatingActionButton(
            onClick = onDiscoverRecipesClick,
            containerColor = themeViewModel.softFabColor,
            contentColor = Color.White,         // white icon
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = "Find opskrifter på engelsk"
            )
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    basePrice: Float, // not used anymore, but kept so list doesn't break
    recipeViewModel: RecipeViewModel,
    themeViewModel: ThemeViewModel,
    selectedStores: Set<Long>,
    onClick: () -> Unit
) {
    var portions by remember { mutableStateOf(4) }  // staart at 4 always
    var price by remember { mutableStateOf(0f) }

    LaunchedEffect(portions, selectedStores) {
        price = recipeViewModel.getRecipePriceByPortions(recipe.id, portions, selectedStores)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {

            // IMAGE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(themeViewModel.secondaryColor)
            ) {
                LocalImage(
                    imagePath = recipe.imagePath,
                    modifier = Modifier.fillMaxSize()
                )

                // PRICE TAG
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(
                            color = themeViewModel.priceTagColor.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = String.format("%.2f kr", price),
                        color = themeViewModel.onPrimaryColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // TITLE + +/- BAR
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {

                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = themeViewModel.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                // Portions selector (same as detail screen)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (portions > 1) portions-- },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.RemoveCircle, null, tint = themeViewModel.priceTagColor)
                    }

                    Text(
                        text = portions.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    IconButton(
                        onClick = { portions++ },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, null, tint = themeViewModel.priceTagColor)
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "pers",
                        color = themeViewModel.textSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}