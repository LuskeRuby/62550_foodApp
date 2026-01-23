package com.example.a62550_foodapp.ui.recipe

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.model.Recipe
import com.example.a62550_foodapp.ui.components.LocalImage
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.BasicTextField
import java.util.Locale

@Composable
fun RecipePage(
    onAddRecipeClick: () -> Unit,
    onDiscoverRecipesClick: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    recipeViewModel: RecipeViewModel = koinViewModel(),
    themeViewModel: ThemeViewModel = koinViewModel()
) {

    // ---------- UI STATE ----------
    var searchText by remember { mutableStateOf("") }
    var portions by remember { mutableStateOf(4) }

    // ---------- DATA ----------
    val filterViewModel: StoreFilterViewModel = koinViewModel()
    val selectedStores by filterViewModel.selectedStores.collectAsState()

    val allRecipes by recipeViewModel
        .getRecipesWithPricesFlow(selectedStores, portions)
        .collectAsState(initial = emptyList())

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


            RecipeHeader(
                searchText = searchText,
                onSearchChange = { searchText = it },
                portions = portions,
                onIncreasePortions = { portions++ },
                onDecreasePortions = { if (portions > 1) portions-- },
                onDiscoverClick = onDiscoverRecipesClick,
                themeViewModel = themeViewModel
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = themeViewModel.textSecondary.copy(alpha = 0.12f)
            )



            // ---------- GRID ----------
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
                            price = price,
                            themeViewModel = themeViewModel,
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = onAddRecipeClick,
            containerColor = themeViewModel.addButtonColor,
            contentColor = themeViewModel.onPrimaryColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }

    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    price: Float,
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(themeViewModel.secondaryColor)
            ) {
                LocalImage(
                    imagePath = recipe.imagePath,
                    modifier = Modifier.fillMaxSize()
                )

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
                        text = String.format(Locale.getDefault(),"%.2f kr", price.toDouble()),
                        color = themeViewModel.onPrimaryColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

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
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }
    }
}
@Composable
fun RecipeHeader(
    searchText: String,
    onSearchChange: (String) -> Unit,
    portions: Int,
    onIncreasePortions: () -> Unit,
    onDecreasePortions: () -> Unit,
    onDiscoverClick: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    var searching by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 2.dp,
        color = themeViewModel.surfaceColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(56.dp)
                .padding(horizontal = 12.dp)
        ) {

            // søg ikon
            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 1.dp,
                color = themeViewModel.cardBackgroundColor,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        searching = !searching
                        if (!searching) onSearchChange("")
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Søg",
                        tint = themeViewModel.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            //søgefelt
            AnimatedVisibility(visible = searching) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 1.dp,
                    color = themeViewModel.cardBackgroundColor,
                    modifier = Modifier
                        .height(36.dp)
                        .widthIn(min = 140.dp, max = 200.dp)
                ) {
                    BasicTextField(
                        value = searchText,
                        onValueChange = onSearchChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = themeViewModel.textPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        decorationBox = { inner ->
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "Søg opskrift",
                                    color = themeViewModel.textSecondary
                                )
                            }
                            inner()
                        }
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            //udforsk
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 1.dp,
                color = themeViewModel.cardBackgroundColor,
                modifier = Modifier
                    .height(36.dp)
                    .clickable(onClick = onDiscoverClick)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        horizontal = if (searching) 10.dp else 14.dp
                    )
                ) {
                    Icon(
                        Icons.Default.Public,
                        contentDescription = "Udforsk",
                        tint = themeViewModel.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )

                    //text gone not serch
                    AnimatedVisibility(visible = !searching) {
                        Row {
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Udforsk",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium,
                                color = themeViewModel.textPrimary
                            )
                        }
                    }
                }
            }


            Spacer(Modifier.weight(1f))

            //portion
            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 1.dp,
                color = themeViewModel.cardBackgroundColor
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(36.dp)
                        .padding(horizontal = if (searching) 10.dp else 8.dp)
                ) {

                    //remove - not search
                    AnimatedVisibility(visible = !searching) {
                        IconButton(
                            onClick = onDecreasePortions,
                            modifier = Modifier.size(28.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = themeViewModel.priceTagColor
                            )
                        ) {
                            Icon(Icons.Default.RemoveCircle, contentDescription = "Minus")
                        }
                    }

                    Text(
                        text = "$portions pers",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = themeViewModel.textPrimary,
                        modifier = Modifier.padding(horizontal = 0.dp)
                    )

                    //remove + when not searh
                    AnimatedVisibility(visible = !searching) {
                        IconButton(
                            onClick = onIncreasePortions,
                            modifier = Modifier.size(28.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = themeViewModel.priceTagColor
                            )
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Plus")
                        }
                    }
                }
            }
        }
    }
}
