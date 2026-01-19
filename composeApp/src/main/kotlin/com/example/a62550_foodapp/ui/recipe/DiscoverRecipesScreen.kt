package com.example.a62550_foodapp.ui.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.a62550_foodapp.model.MealCategory
import com.example.a62550_foodapp.model.MealSummary
import org.koin.androidx.compose.koinViewModel
import com.example.a62550_foodapp.viewmodel.DiscoverRecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverRecipesScreen(
    onBack: () -> Unit,
    onMealClick: (String) -> Unit,
    viewModel: DiscoverRecipeViewModel = koinViewModel(),
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categoryListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }
    //Start text
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Opskrifter fra TheMealDB") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    )
    //Kateogrier
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeViewModel.backgroundColor)
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                //loading api feedback
                loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = themeViewModel.primaryColor
                        )
                    }
                }

                error != null -> {
                    Text(
                        text = error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                //load succes display
                else -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Vælg kategori",
                            style = MaterialTheme.typography.titleMedium,
                            color = themeViewModel.textPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            state = categoryListState,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(categories) { category ->
                                CategoryCard(
                                    category = category,
                                    selected = category.name == selectedCategory,
                                    onClick = {
                                        viewModel.selectCategory(category.name)
                                    },
                                    themeViewModel = themeViewModel
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        //Opskriftsliste
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            items(meals) { meal ->
                                MealCard(
                                    meal = meal,
                                    onClick = {
                                        onMealClick(meal.id)
                                    },
                                    themeViewModel = themeViewModel
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}


@Composable
fun CategoryCard(
    category: MealCategory,
    selected: Boolean,
    onClick: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    Card(
        modifier = Modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 2.dp else 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                themeViewModel.primaryColor
            else
                themeViewModel.surfaceColor
        )
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.name,
                color = if (selected)
                    themeViewModel.onPrimaryColor
                else
                    themeViewModel.textPrimary,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        }
    }
}
@Composable
fun MealCard(
    meal: MealSummary,
    onClick: () -> Unit,
    themeViewModel: ThemeViewModel
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

            //billed
            AsyncImage(
                model = meal.thumbnail,
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )

            //titel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = themeViewModel.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
