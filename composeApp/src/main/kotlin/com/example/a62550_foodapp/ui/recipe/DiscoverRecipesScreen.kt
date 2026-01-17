package com.example.a62550_foodapp.ui.discover

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.a62550_foodapp.model.MealCategory
import com.example.a62550_foodapp.model.MealSummary
import org.koin.androidx.compose.koinViewModel
import com.example.a62550_foodapp.viewmodel.DiscoverRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverRecipesScreen(
    onBack: () -> Unit,
    onMealClick: (String) -> Unit,
    viewModel: DiscoverRecipeViewModel = koinViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }
    //Start text
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Opskrifter hentet fra TheMealDB")
                },
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
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                loading -> CircularProgressIndicator()

                error != null -> Text(
                    text = error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error
                )

                else -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Vælg kategori",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(categories) { category ->
                                CategoryCard(
                                    category = category,
                                    onClick = {
                                        viewModel.selectCategory(category.name)
                                    },
                                    selected = category.name == selectedCategory
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        //Opskriftsliste
                        when {
                            loading -> {
                                CircularProgressIndicator()
                            }

                            meals.isEmpty() -> {
                                Text(
                                    text = "Vælg en kategori ovenfor for at se opskrifter",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            else -> {
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
                                            }
                                        )
                                    }
                                }
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
    onClick: () -> Unit
) {
    val containerColor =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant

    val textColor =
        if (selected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .height(48.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                maxLines = 1
            )
        }
    }
}
@Composable
fun MealCard(meal: MealSummary, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            AsyncImage(
                model = meal.thumbnail,
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = meal.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp),
                maxLines = 2
            )
        }
    }
}
