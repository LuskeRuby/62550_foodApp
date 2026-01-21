package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.util.Locale
import androidx.compose.foundation.ScrollState
import org.koin.androidx.compose.koinViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel



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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(0.6f), Color.Transparent)
                    )
                )
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
        ) {
            Icon(Icons.Default.Close, null, tint = Color.White)
        }

        onEdit?.let {
            IconButton(
                onClick = it,
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
            ) {
                Icon(Icons.Default.Edit, null, tint = Color.White)
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomCenter)
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

            LazyColumn {
                items(shoppingLists, key = { it.id }) { list ->
                    ListItem(
                        headlineContent = { Text(list.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onShoppingListSelected(list) }
                    )
                }
            }

            Divider()

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
