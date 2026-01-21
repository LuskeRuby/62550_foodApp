package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel
import androidx.compose.material3.Text
import com.example.a62550_foodapp.viewmodel.ShoppingListViewModel

@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    recipeViewModel: RecipeViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val recipe by recipeViewModel.getRecipeById(recipeId).collectAsState(initial = null)
    val recipeItems by recipeViewModel
        .getItemsForRecipeFlow(recipeId)
        .collectAsState(initial = emptyList())
    val storeFilterViewModel: StoreFilterViewModel = koinViewModel()
    val selectedStores by storeFilterViewModel.selectedStores.collectAsState()
    val shoppingListViewModel: ShoppingListViewModel = koinViewModel()
    val shoppingLists by shoppingListViewModel.shoppingLists.collectAsState()

    var showCreateShoppingListOverlay by remember { mutableStateOf(false) }
    var portions by remember { mutableStateOf(4) }
    var scaledPrice by remember { mutableStateOf(0f) }
    var showIngredients by remember { mutableStateOf(true) }
    var ingredients by remember { mutableStateOf<List<Triple<String, Int, String>>>(emptyList()) }
    var showAddToListSheet by remember { mutableStateOf(false) }
    val shoppingListUi = shoppingLists.map {
        ShoppingListUi(
            id = it.id,
            name = it.name
        )
    }
    LaunchedEffect(portions, selectedStores) {
        scaledPrice = recipeViewModel.getRecipePriceByPortions(
            recipeId = recipeId,
            portions = portions,
            selectedStores = selectedStores
        )
    }
        LaunchedEffect(recipeItems, portions, selectedStores) {
            ingredients = recipeViewModel.resolveIngredients(
                items = recipeItems,
                portions = portions
            )
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
                    showAddToListSheet = true
                },
                themeViewModel = themeViewModel
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = showIngredients,
                    onClick = { showIngredients = true },
                    label = { Text("Ingredienser") }
                )

                FilterChip(
                    selected = !showIngredients,
                    onClick = { showIngredients = false },
                    label = { Text("Beskrivelse") }
                )
            }

            //Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                if (showIngredients) {

                    IngredientsCard(
                        ingredients = ingredients.map { (name, qty, unit) ->
                            name to "$qty $unit"
                        }
                    )

                } else {

                    InstructionsCard(
                        instructions = r.instructions
                    )
                }
            }
        }

    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
    AddRecipeToShoppingListSheet(
        visible = showAddToListSheet,
        shoppingLists = shoppingListUi,
        onDismiss = { showAddToListSheet = false },
        onShoppingListSelected = { list ->
            showAddToListSheet = false
            recipeViewModel.addRecipeToShoppingList(
                shoppingListId = list.id,
                recipeId = recipeId,
                portions = portions
            )
        },
        onCreateNewShoppingList = {
            showAddToListSheet = false
            showCreateShoppingListOverlay = true
        }
    )



}
