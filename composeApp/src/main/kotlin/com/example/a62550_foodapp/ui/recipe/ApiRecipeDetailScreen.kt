package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.api.dto.toApiIngredients
import com.example.a62550_foodapp.viewmodel.ApiRecipeDetailViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ApiRecipeDetailScreen(
    mealId: String,
    onBack: () -> Unit,
    viewModel: ApiRecipeDetailViewModel = koinViewModel(),
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val meal by viewModel.meal.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(mealId) {
        viewModel.load(mealId)
    }

    val scrollState = rememberScrollState()

    when {
        loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = themeViewModel.primaryColor
                )
            }
        }

        meal != null -> {
            val m = meal!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                //header
                RecipeHeaderCollapsing(
                    imageUrl = m.strMealThumb,
                    title = m.strMeal,
                    scrollState = scrollState,
                    onBack = onBack,
                    onEdit = null //hentet fra api kan ikke ædnres
                )

                ApiRecipeActionBar(
                    onAdd = { /* MANGLER IMPLEMENT */ },
                    themeViewModel = themeViewModel
                )

                //Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(bottom = 24.dp)
                ) {
                    Spacer(Modifier.height(16.dp))

                    IngredientsCard(
                        ingredients = m.toApiIngredients().map {
                            it.name to it.measure
                        }
                    )

                    InstructionsCard(
                        instructions = m.strInstructions
                    )
                }
            }
        }
    }
}

@Composable
fun ApiRecipeActionBar(
    onAdd: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TheMealDB", fontWeight = FontWeight.Bold)
                Text("Opskrifts Kilde", color = themeViewModel.priceTagColor)
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onAdd)
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = themeViewModel.priceTagColor
                )
                Text("Tilføj", color = themeViewModel.priceTagColor)
            }
        }
    }
}
