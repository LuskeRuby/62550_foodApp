package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.api.dto.toApiIngredients
import com.example.a62550_foodapp.viewmodel.ApiRecipeDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ApiRecipeDetailScreen(
    mealId: String,
    viewModel: ApiRecipeDetailViewModel = koinViewModel(),
    onBack: () -> Unit
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
                CircularProgressIndicator()
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
