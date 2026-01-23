package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.util.Locale
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import org.koin.androidx.compose.koinViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// ---------- HEADER ----------

@Composable
fun RecipeHeaderCollapsing(
    imageUrl: String?,
    title: String,
    scrollState: ScrollState,
    onBack: () -> Unit,
    onEdit: (() -> Unit)? = null
) {
    val maxHeight = 260.dp
    val minHeight = 96.dp
    val collapse = (scrollState.value / 300f).coerceIn(0f, 1f)
    val height = maxHeight - (maxHeight - minHeight) * collapse

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ----- DARK OVERLAY -----
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        // ----- BACK (TOP LEFT) -----
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // ----- EDIT (TOP RIGHT) -----
        onEdit?.let {
            IconButton(
                onClick = it,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit recipe",
                    tint = Color.White
                )
            }
        }

        // ----- TITLE (BOTTOM) -----
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

// ---------- ACTION BAR ----------

@Composable
fun RecipeActionBar(
    portions: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    preparationMinutes: Int,
    price: Float,
    onAdd: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    Surface(tonalElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    IconButton(onClick = onDecrease) {
                        Icon(Icons.Default.RemoveCircle, null, tint = themeViewModel.priceTagColor)
                    }
                    Text(portions.toString(), fontWeight = FontWeight.Bold)
                    IconButton(onClick = onIncrease) {
                        Icon(Icons.Default.AddCircle, null, tint = themeViewModel.priceTagColor)
                    }
                }
                Text("Personer", color = themeViewModel.priceTagColor)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⏱ $preparationMinutes min", fontWeight = FontWeight.Bold)
                Text("Tid", color = themeViewModel.priceTagColor)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    String.format(Locale.getDefault(), "%.2f kr", price),
                    fontWeight = FontWeight.Bold
                )
                Text("Pris", color = themeViewModel.priceTagColor)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onAdd)
            ) {
                Icon(Icons.Default.AddCircle, null, tint = themeViewModel.priceTagColor)
                Text("Tilføj", color = themeViewModel.priceTagColor)
            }
        }
    }
}

// ---------- CARDS ----------

@Composable
fun IngredientsCard(
    ingredients: List<Pair<String, String>>,
    emptyText: String = "Ingen ingredienser angivet."
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = koinViewModel<ThemeViewModel>().cardBackgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Ingredienser", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (ingredients.isEmpty()) {
                Text(emptyText)
            } else {
                ingredients.forEach { (name, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(name)
                        Text(value)
                    }
                }
            }
        }
    }
}

@Composable
fun InstructionsCard(instructions: String?) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = koinViewModel<ThemeViewModel>().cardBackgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            instructions ?: "Ingen instruktioner",
            modifier = Modifier.padding(16.dp),
            lineHeight = 20.sp
        )
    }
}

// ---------- BOTTOM SHEET ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeToShoppingListSheet(
    visible: Boolean,
    shoppingLists: List<ShoppingListUi>,
    onDismiss: () -> Unit,
    onShoppingListSelected: (ShoppingListUi) -> Unit,
    onCreateNewShoppingList: () -> Unit
) {
    if (!visible) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Tilføj til indkøbsliste",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

            }

            Spacer(Modifier.height(4.dp))

            LazyColumn {
                itemsIndexed(shoppingLists, key = { _, it -> it.id }) { index, list ->
                    ListItem(
                        leadingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        headlineContent = {
                            Text(list.name)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onShoppingListSelected(list) }
                    )

                    if (index < shoppingLists.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }

            HorizontalDivider()

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCreateNewShoppingList
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Ny indkøbsliste")
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun RecipeDetailLayout(
    title: String,
    imagePath: String?,
    preparationMinutes: Int,
    price: Float,
    portions: Int,
    onDecreasePortions: () -> Unit,
    onIncreasePortions: () -> Unit,
    ingredients: List<Pair<String, String>>,
    description: String?,
    instructions: String?,
    scrollState: ScrollState,
    onBack: () -> Unit,
    onEdit: (() -> Unit)?,
    onAddToList: (() -> Unit)?,
    themeViewModel: ThemeViewModel
) {
    val collapseProgress = (scrollState.value / 300f).coerceIn(0f, 1f)

    var actionBarHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
    ) {

        RecipeHeaderCollapsing(
            imageUrl = imagePath,
            title = title,
            scrollState = scrollState,
            onBack = onBack,
            onEdit = onEdit
        )

        Box(
            modifier = Modifier.onSizeChanged {
                actionBarHeight = with(density) { it.height.toDp() }
            }
        ) {
            RecipeActionBarLocal(
                portions = portions,
                onDecrease = onDecreasePortions,
                onIncrease = onIncreasePortions,
                preparationMinutes = preparationMinutes,
                price = price,
                onAdd = { onAddToList?.invoke() },
                themeViewModel = themeViewModel
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(
                    top = actionBarHeight * collapseProgress,
                    bottom = 24.dp
                )
        ) {

            Spacer(Modifier.height(8.dp))

            IngredientsCard(ingredients = ingredients)

            Spacer(Modifier.height(24.dp))

            if (!description.isNullOrBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = themeViewModel.cardBackgroundColor,
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Beskrivelse", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(description, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = themeViewModel.cardBackgroundColor,
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Fremgangsmåde", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        instructions ?: "Ingen instruktioner angivet.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

fun showRecipeAddedSnackbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    scope.launch {
        snackbarHostState.showSnackbar(
            message = "Tilføjet til indkøbsliste"
        )
    }
}
