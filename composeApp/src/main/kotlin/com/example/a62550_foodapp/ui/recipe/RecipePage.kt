package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Public


@Composable
fun RecipePage(
    onAddRecipeClick: () -> Unit,
    onDiscoverRecipesClick: () -> Unit,
    onRecipeClick: (Int) -> Unit,
    recipeViewModel: RecipeViewModel = koinViewModel(),
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    // --- DATA ---
    val allRecipes by recipeViewModel
        .getRecipesWithPricesFlow()
        .collectAsState(initial = emptyList())

    val allSupermarkets by recipeViewModel.allSupermarkets
        .collectAsState(initial = emptyList())

    val selectedSupermarkets by recipeViewModel.selectedSupermarkets
        .collectAsState()

    // --- UI STATE ---
    var showFilterDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val visibleRecipes = remember(allRecipes, searchText) {
        if (searchText.isBlank()) {
            allRecipes
        } else {
            allRecipes.filter { (recipe, _) ->
                recipe.title.contains(searchText, ignoreCase = true)
            }
        }
    }

    val gridState = rememberLazyGridState()

    val showSearchBar by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0 &&
                    gridState.firstVisibleItemScrollOffset == 0
        }
    }

    val searchBarHeight by animateDpAsState(
        targetValue = if (showSearchBar) 56.dp else 0.dp,
        label = "searchBarHeight"
    )

    // --- UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ===== FILTER ROW =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showFilterDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeViewModel.primaryColor
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = if (selectedSupermarkets.isEmpty()) "Alle Butikker"
                        else if (selectedSupermarkets.size == 1) "${selectedSupermarkets.size} Butik"
                        else "${selectedSupermarkets.size} Butikker",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                if (selectedSupermarkets.isNotEmpty()) {
                    TextButton(
                        onClick = { recipeViewModel.clearSupermarketFilter() }
                    ) {
                        Text("Clear")
                    }
                }
            }

            // ===== SEARCH BAR =====
            // ===== SEARCH + DISCOVER ROW =====
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                if (showSearchBar) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // 🔍 Search field
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Søg opskrift") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            }
                        )

                        // 🌍 Discover button
                        FloatingActionButton(
                            onClick = onDiscoverRecipesClick,
                            containerColor = themeViewModel.secondaryColor,
                            contentColor = themeViewModel.onSecondaryColor,
                            modifier = Modifier.size(48.dp), // smaller than normal FAB
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Find opskrifter på engelsk"
                            )
                        }
                    }
                }
            }

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
                        top = 16.dp,
                        bottom = 16.dp
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
                            price = price,
                            themeViewModel = themeViewModel,
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }

        // ===== CREATE RECIPE BOTTOM =====
        FloatingActionButton(
            onClick = onAddRecipeClick,
            containerColor = themeViewModel.addButtonColor,
            contentColor = themeViewModel.onPrimaryColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp) // above bottom nav, to the right
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Recipe")
        }


        // ===== DISCOVER (GLOBUS) BUTTON =====
        FloatingActionButton(
            onClick = onDiscoverRecipesClick,
            containerColor = themeViewModel.secondaryColor,
            contentColor = themeViewModel.onSecondaryColor,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Public, // 🌍 globus icon
                contentDescription = "Find opskrifter på engelsk"
            )
        }


        // ===== FILTER DIALOG =====
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text("Filter butikker") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allSupermarkets.forEach { supermarket ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        recipeViewModel.toggleSupermarket(supermarket.id)
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = supermarket.id in selectedSupermarkets,
                                    onCheckedChange = {
                                        recipeViewModel.toggleSupermarket(supermarket.id)
                                    }
                                )
                                Text(
                                    text = supermarket.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text("Done")
                    }
                }
            )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {

            // ===== IMAGE =====
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

                // PRICE — bottom right on image
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

            // ===== TITLE AREA =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp) // fixed title area
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = themeViewModel.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


