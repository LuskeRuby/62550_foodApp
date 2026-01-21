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

    // Scroll state also drives the collapsing header animation
    val contentScrollState = rememberScrollState()
    val collapseProgress = (contentScrollState.value / 300f).coerceIn(0f, 1f)

    // Used so content starts below the action bar when header is collapsed
    var actionBarHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    recipe?.let { r ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeViewModel.backgroundColor)
        ) {

            // Top image + title that collapses on scroll
            RecipeHeaderCollapsing(
                imageUrl = r.imagePath,
                title = r.title,
                scrollState = contentScrollState,
                onBack = onBack,
                onEdit = { onEdit(r.id) }
            )

            // Sticky action bar under the header
            Box(
                modifier = Modifier.onSizeChanged {
                    actionBarHeight = with(density) { it.height.toDp() }
                }
            ) {
                RecipeActionBarLocal(
                    portions = portions,
                    onDecrease = { if (portions > 1) portions-- },
                    onIncrease = { portions++ },
                    preparationMinutes = r.preparationTimeMinutes,
                    price = scaledPrice,
                    onAdd = { showAddToListSheet = true },
                    themeViewModel = themeViewModel
                )
            }

            // Scrollable content that moves under the action bar when header is collapsed
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(contentScrollState)
                    .padding(
                        top = actionBarHeight * collapseProgress,
                        bottom = 24.dp
                    )
            ) {

                Spacer(Modifier.height(8.dp))

                IngredientsCard(
                    ingredients = ingredients.map { (name, qty, unit) ->
                        name to "$qty $unit"
                    }
                )

                Spacer(Modifier.height(24.dp))

                // Description card (only shown if present)
                if (!r.description.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = themeViewModel.cardBackgroundColor,
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                text = "Beskrivelse",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = r.description!!,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }

                // Instructions card (with fallback if missing)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = themeViewModel.cardBackgroundColor,
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = "Fremgangsmåde",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        r.instructions?.let { instructionsText ->
                            Text(
                                text = instructionsText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } ?: Text(
                            text = "Ingen instruktioner angivet.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

    // Bottom sheet for choosing shopping list
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
}

@Composable
private fun RecipeActionBarLocal(
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
