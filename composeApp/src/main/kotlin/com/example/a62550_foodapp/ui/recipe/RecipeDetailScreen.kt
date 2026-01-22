package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.viewmodel.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.ScrollState

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

    val scope = rememberCoroutineScope()

    // UI state
    var portions by remember { mutableStateOf(4) }
    var scaledPrice by remember { mutableStateOf(0f) }
    var ingredients by remember { mutableStateOf<List<Triple<String, Int, String>>>(emptyList()) }

    var showAddToListSheet by remember { mutableStateOf(false) }
    var showCreateShoppingListDialog by remember { mutableStateOf(false) }
    var newShoppingListName by remember { mutableStateOf("") }

    val shoppingListUi = shoppingLists.map {
        ShoppingListUi(id = it.id, name = it.name)
    }

    // Recalculate price when portions or selected stores change
    LaunchedEffect(portions, selectedStores) {
        scaledPrice = recipeViewModel.getRecipePriceByPortions(
            recipeId = recipeId,
            portions = portions,
            selectedStores = selectedStores
        )
    }

    // Recalculate ingredient amounts when portions change
    LaunchedEffect(recipeItems, portions) {
        ingredients = recipeViewModel.resolveIngredients(
            items = recipeItems,
            portions = portions
        )
    }

    recipe?.let { r ->

        val scrollState = rememberScrollState()

        RecipeDetailLayout(
            title = r.title,
            imagePath = r.imagePath,
            preparationMinutes = r.preparationTimeMinutes,
            price = scaledPrice,
            portions = portions,
            onDecreasePortions = { if (portions > 1) portions-- },
            onIncreasePortions = { portions++ },
            ingredients = ingredients.map { (n, q, u) -> n to "$q $u" },
            description = r.description,
            instructions = r.instructions,
            scrollState = scrollState,
            onBack = onBack,
            onEdit = { onEdit(r.id) },
            onAddToList = { showAddToListSheet = true },
            themeViewModel = themeViewModel
        )

    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

    // Dialog for creating a new shopping list
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
                        val name = newShoppingListName.trim()
                        if (name.isNotBlank()) {
                            scope.launch {
                                val newListId =
                                    shoppingListViewModel.createShoppingListAndReturnId(name)

                                recipeViewModel.addRecipeToShoppingList(
                                    shoppingListId = newListId,
                                    recipeId = recipeId,
                                    portions = portions
                                )
                            }
                        }
                        newShoppingListName = ""
                        showCreateShoppingListDialog = false
                    }
                ) { Text("Opret") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        newShoppingListName = ""
                        showCreateShoppingListDialog = false
                    }
                ) { Text("Annuller") }
            }
        )
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
            showCreateShoppingListDialog = true
        }
    )

}

@Composable
fun RecipeActionBarLocal(
    portions: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    preparationMinutes: Int,
    price: Float,
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
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Portions
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.RemoveCircle,
                        contentDescription = "Decrease portions",
                        tint = themeViewModel.priceTagColor,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable(onClick = onDecrease)
                    )

                    Text(
                        text = portions.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "Increase portions",
                        tint = themeViewModel.priceTagColor,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable(onClick = onIncrease)
                    )
                }

                Text(
                    text = "personer",
                    style = MaterialTheme.typography.bodySmall,
                    color = themeViewModel.priceTagColor
                )
            }

            // Time
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = preparationMinutes.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "min",
                    style = MaterialTheme.typography.bodySmall,
                    color = themeViewModel.priceTagColor
                )
            }

            // Price
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.2f", price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "kr",
                    style = MaterialTheme.typography.bodySmall,
                    color = themeViewModel.priceTagColor
                )
            }

            // Add
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onAdd)
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = "Add to shopping list",
                    tint = themeViewModel.priceTagColor,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Tilføj",
                    style = MaterialTheme.typography.bodySmall,
                    color = themeViewModel.priceTagColor
                )
            }
        }
    }
}
