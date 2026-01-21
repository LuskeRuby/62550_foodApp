package com.example.a62550_foodapp.ui.shoppingList

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a62550_foodapp.db.projection.ShoppingListEntry
import com.example.a62550_foodapp.ui.components.SearchSelectField
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

// ------------------------------------------------------------
// ENTRY
// ------------------------------------------------------------

@Composable
fun ShoppingListDetailsPage(shoppingListId: Long) {
    var addOverlay by remember { mutableStateOf(false) }

    if (!addOverlay) {
        ShoppingListPage(
            shoppingListId = shoppingListId,
            onAdd = { addOverlay = true }
        )
    } else {
        BackHandler { addOverlay = false }
        AddItemGroupToShoppingListPage(
            shoppingListId = shoppingListId,
            disableItemOverlay = { addOverlay = false }
        )
    }
}

// ------------------------------------------------------------
// MAIN PAGE
// ------------------------------------------------------------

@Composable
private fun ShoppingListPage(
    shoppingListId: Long,
    onAdd: () -> Unit,
    theme: ThemeViewModel = koinViewModel(),
    viewModel: ShoppingListDetailsViewModel =
        koinViewModel(
            key = "ShoppingListDetails-$shoppingListId",
            parameters = { parametersOf(shoppingListId) }
        )
) {
    val items by viewModel.items.collectAsState()
    val total by viewModel.shoppingListTotalPrice.collectAsState()

    val storeFilterVM: StoreFilterViewModel = koinViewModel()
    val selectedStores by storeFilterVM.selectedStores.collectAsState()

    LaunchedEffect(selectedStores) {
        viewModel.setStoreFilter(selectedStores)
    }

    // Store → Category → Items
    val grouped: Map<String, Map<String, List<ShoppingListEntry>>> =
        items
            .groupBy { it.superMarketName.orEmpty() }
            .mapValues { (_, list) ->
                list.groupBy { it.category.ifBlank { "Ukendt kategori" } }
            }

    Box(Modifier.fillMaxSize().background(theme.backgroundColor)) {
        Column {
            Header(total)

            ShoppingListContent(
                grouped = grouped,
                onCheck = { item, checked -> viewModel.setCheckmark(item, checked) },
                onDelete = { viewModel.delete(it) }
            )
        }

        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            containerColor = theme.addButtonColor
        ) {
            Text("Tilføj varer")
        }
    }
}

// ------------------------------------------------------------
// LIST CONTENT
// ------------------------------------------------------------

@Composable
private fun ShoppingListContent(
    grouped: Map<String, Map<String, List<ShoppingListEntry>>>,
    onCheck: (ShoppingListEntry, Boolean) -> Unit,
    onDelete: (ShoppingListEntry) -> Unit
) {
    LazyColumn {
        grouped.forEach { (store, categoryMap) ->

            if (store.isNotBlank()) {
                item { SuperMarketHeader(store) }
            }

            categoryMap.forEach { (category, items) ->
                item { CategoryHeader(category) }

                items(
                    items = items,
                    key = { "${it.id}-${it.itemGroupId}-${it.superMarketName}" }
                ) { entry ->
                    ShoppingItemRow(
                        item = entry,
                        onCheckedChange = { checked -> onCheck(entry, checked) },
                        onDelete = { onDelete(entry) }
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------
// UI PIECES
// ------------------------------------------------------------

@Composable
fun SuperMarketHeader(name: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFF6200EE))
            .padding(8.dp)
    ) {
        Text(name, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CategoryHeader(category: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(6.dp)
    ) {
        Text(category, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingItemRow(
    item: ShoppingListEntry,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    theme: ThemeViewModel = koinViewModel()
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, null, tint = Color.White)
            }
        }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(
                    if (item.isChecked) theme.fadedBackground else theme.backgroundColor
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = item.isChecked,
                onCheckedChange = onCheckedChange
            )

            Text(
                "${item.quantity} × ${item.itemName}",
                Modifier.weight(1f),
                textDecoration =
                    if (item.isChecked) TextDecoration.LineThrough else null
            )

            Text(
                item.price?.let { "${(it * item.quantity).toInt()} kr" } ?: "—",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ------------------------------------------------------------
// HEADER
// ------------------------------------------------------------

@Composable
private fun Header(total: ShoppingListDetailsViewModel.TotalUi) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFF2E7D32),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total ${total.total.toInt()} kr", color = Color.White, fontWeight = FontWeight.Bold)
            if (total.missingCount > 0) {
                Text("* Ufuldstændig pris", color = Color.White)
            }
        }
    }
}
