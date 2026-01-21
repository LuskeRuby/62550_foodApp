package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel
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
    val shoppingListUi = shoppingLists.map {
        ShoppingListUi(
            id = it.id,
            name = it.name
        )
    }

    var portions by remember { mutableStateOf(1) }
    var scaledPrice by remember { mutableStateOf(0f) }
    var showAddToListSheet by remember { mutableStateOf(false) }
    var showCreateShoppingListDialog by remember { mutableStateOf(false) }
    var newShoppingListName by remember { mutableStateOf("") }

    LaunchedEffect(portions) {
        scaledPrice = recipeViewModel.getRecipePriceByPortions(
            recipeId = recipeId,
            portions = portions,
            selectedStores = selectedStores
        )
    }
/*
    LaunchedEffect(recipeItems, portions) {
        ingredients = recipeViewModel.resolveIngredients(
            items = recipeItems,
            portions = portions
        )
    }
 */

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

            /*
            onAdd = {
                    recipeViewModel.addRecipeToShoppingList(
                        shoppingListId = 1L,
                        recipeId = recipeId,
                        portions = portions
                    )
                }
             */

            //Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                /*
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

                 */

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

    AddRecipeToShoppingListSheet(
        visible = showAddToListSheet,
        shoppingLists = shoppingListUi,
        onDismiss = { showAddToListSheet = false },
        onShoppingListSelected = {
            showAddToListSheet = false
            // later: real DB call
        },
        onCreateNewShoppingList = {
            showAddToListSheet = false
            showCreateShoppingListDialog = true
        }
    )

    if (showCreateShoppingListDialog) {
        AlertDialog(
            onDismissRequest = { showCreateShoppingListDialog = false },
            title = { Text("Ny indkøbsliste") },
            text = {
                OutlinedTextField(
                    value = newShoppingListName,
                    onValueChange = { newShoppingListName = it },
                    label = { Text("Navn") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newShoppingListName.isNotBlank()) {
                            shoppingListViewModel.createShoppingList(newShoppingListName)
                        }
                        newShoppingListName = ""
                        showCreateShoppingListDialog = false
                    }
                ) {
                    Text("Opret")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        newShoppingListName = ""
                        showCreateShoppingListDialog = false
                    }
                ) {
                    Text("Annuller")
                }
            }
        )
    }


}

