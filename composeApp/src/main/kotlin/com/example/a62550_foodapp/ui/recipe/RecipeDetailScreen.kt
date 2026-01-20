package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.model.Ingredient
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    recipeViewModel: RecipeViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val recipe by recipeViewModel.getRecipeById(recipeId).collectAsState(initial = null)
    val recipeItems by recipeViewModel
        .getItemsForRecipeFlow(recipeId)
        .collectAsState(initial = emptyList())

    var portions by remember { mutableStateOf(1) }
    var scaledPrice by remember { mutableStateOf(0f) }
    var ingredients by remember { mutableStateOf<List<Ingredient>>(emptyList()) }

    LaunchedEffect(portions) {
        scaledPrice = recipeViewModel.getRecipePriceByPortions(recipeId, portions)
    }

    LaunchedEffect(recipeItems, portions) {
        ingredients = recipeItems.map {
            recipeViewModel.resolveIngredient(
                itemGroupId = it.itemGroupId,
                quantity = it.sizeOfOnePortion * portions
            )
        }
    }

    val scrollState = rememberScrollState()

    recipe?.let { r ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeViewModel.backgroundColor)
        ) {

            //Header
            RecipeHeaderCollapsing(
                imageUrl = r.imagePath,
                title = r.title,
                scrollState = scrollState,
                onBack = onBack,
                onEdit = { onEdit(r.id) }
            )

            //Actionbar
            RecipeActionBar(
                portions = portions,
                onDecrease = { if (portions > 1) portions-- },
                onIncrease = { portions++ },
                preparationMinutes = r.preparationTimeMinutes,
                price = scaledPrice,
                onAdd = {
                    recipeViewModel.addRecipeToShoppingList(
                        shoppingListId = 1, // todo én aktiv liste
                        recipeId = recipeId,
                        portions = portions
                    )
                },
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
                    ingredients = ingredients.map {
                        it.groupName to buildString {
                            val q = it.quantity
                            append(
                                if (q % 1f == 0f) q.toInt()
                                else String.format(Locale.getDefault(), "%.1f", q)
                            )
                            append(" ")
                            append(it.unitType)
                        }
                    }
                )

                InstructionsCard(
                    instructions = r.instructions
                )
            }
        }

    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
