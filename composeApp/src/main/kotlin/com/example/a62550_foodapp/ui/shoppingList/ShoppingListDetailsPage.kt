package com.example.a62550_foodapp.ui.shoppingList

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a62550_foodapp.db.projection.ShoppingListEntry
import com.example.a62550_foodapp.ui.components.SearchSelectField
import com.example.a62550_foodapp.viewmodel.ItemGroupViewModel
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

// PAGE
@Composable
fun ShoppingListDetailsPage(
    shoppingListId: Long
) {
    var addItemsOverlay by remember { mutableStateOf(false) }

    if (!addItemsOverlay) {
        ShoppingListPage(
            shoppingListId = shoppingListId,
            onAddItemsButtonClick = { addItemsOverlay = true }
        )
    } else {
        BackHandler { addItemsOverlay = false }
        AddItemToShoppingListPage(
            shoppingListId = shoppingListId,
            disableItemOverlay = { addItemsOverlay = false }
        )
    }
}

// MAIN LIST PAGE
@Composable
private fun ShoppingListPage(
    shoppingListId: Long,
    onAddItemsButtonClick: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel(),
    viewModel: ShoppingListDetailsViewModel =
        koinViewModel(
            key = "ShoppingListDetails-$shoppingListId",
            parameters = { parametersOf(shoppingListId) }
        )
) {
    val items by viewModel.items.collectAsState()
    val totalUi by viewModel.shoppingListTotalPrice.collectAsState()

    val grouped =
        items.groupBy { it.superMarketName }
            .mapValues { (_, entries) ->
                entries.groupBy { it.category }
            }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
    ) {
        Column {

            Text(
                text = "Indkøbsliste",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            ShoppingListContent(
                viewModel = viewModel,
                grouped = grouped,
                checkboxesEnabled = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TotalFooter(totalUi)
            }
        }

        FloatingActionButton(
            onClick = onAddItemsButtonClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = themeViewModel.addButtonColor,
            contentColor = themeViewModel.onPrimaryColor
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add item")
        }
    }
}

// ADD ITEMS OVERLAY
@Composable
private fun AddItemToShoppingListPage(
    shoppingListId: Long,
    disableItemOverlay: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel(),
    viewModel: ShoppingListDetailsViewModel =
        koinViewModel(
            key = "ShoppingListDetails-$shoppingListId",
            parameters = { parametersOf(shoppingListId) }
        )
) {
    val itemGroupViewModel: ItemGroupViewModel = koinViewModel()
    val itemGroups by itemGroupViewModel.itemGroups.collectAsState()
    val items by viewModel.items.collectAsState()

    val grouped =
        items.groupBy { it.superMarketName }
            .mapValues { (_, entries) ->
                entries.groupBy { it.category }
            }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Tilføj til indkøbslisten",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        SearchSelectField(
            label = "Søg varer",
            items = itemGroups,
            itemText = { it.name },
            itemUnit = { it.unitType },
            onItemSelected = { group ->
                viewModel.add(
                    addedItem = group,
                    portionQuantity = 1,
                    portionSize = 0f
                )
            }
        )

        ShoppingListContent(
            viewModel = viewModel,
            grouped = grouped,
            checkboxesEnabled = false
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = disableItemOverlay,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF269900))
            ) {
                Text("Færdig")
            }

            Button(
                onClick = disableItemOverlay,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Tilbage")
            }
        }
    }
}


// LIST CONTENT
@Composable
private fun ShoppingListContent(
    viewModel: ShoppingListDetailsViewModel,
    grouped: Map<String, Map<String, List<ShoppingListEntry>>>,
    checkboxesEnabled: Boolean
) {
    LazyColumn {
        grouped.forEach { (supermarket, categoryMap) ->

            item { SuperMarketHeader(supermarket) }

            categoryMap.forEach { (category, entries) ->
                item { CategoryHeader(category) }

                items(
                    entries,
                    key = { "${it.id}-${it.superMarketName}" }
                ) { entry ->
                    ShoppingItemRow(
                        item = entry,
                        onCheckedChange =
                            if (checkboxesEnabled) {
                                { viewModel.setCheckmark(entry, it) }
                            } else ({ }),
                        checkboxVisible = checkboxesEnabled,
                        onDelete =
                            if (checkboxesEnabled) {
                                { viewModel.delete(entry) }
                            } else ({ })
                    )
                }
            }
        }
    }
}

// HEADERS
@Composable
private fun SuperMarketHeader(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF6200EE))
            .padding(8.dp)
    ) {
        Text(text = name, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CategoryHeader(
    category: String,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(categoryColor(category, themeViewModel))
            .padding(6.dp)
    ) {
        Text(
            text = category,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

// ROW
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingItemRow(
    item: ShoppingListEntry,
    onCheckedChange: (Boolean) -> Unit,
    checkboxVisible: Boolean,
    onDelete: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
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
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (item.isChecked) themeViewModel.fadedBackground
                    else themeViewModel.backgroundColor,
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            CheckboxText(
                checked = item.isChecked,
                text = "${item.quantity} x ${item.itemName}",
                modifier = Modifier.weight(1f),
                color = themeViewModel.textPrimary,
                fontWeight = FontWeight.Medium
            )

            CheckboxText(
                checked = item.isChecked,
                text = "${item.size.toInt()} ${item.unitType}",
                modifier = Modifier.width(80.dp),
                textAlign = TextAlign.End,
                color = themeViewModel.textPrimary
            )

            CheckboxText(
                checked = item.isChecked,
                text = item.price?.let { "${(it * item.quantity).toInt()} kr" } ?: "Utilgængelig",
                modifier = Modifier.width(70.dp),
                textAlign = TextAlign.End,
                color = themeViewModel.priceTagColor,
                fontWeight = FontWeight.Bold
            )

            if (checkboxVisible) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }
}

// HELPERS
@Composable
private fun CheckboxText(
    checked: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    color: Color,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    Text(
        text = text,
        modifier = modifier,
        color = if (checked) themeViewModel.greyedOutColor else color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
    )
}

@Composable
private fun TotalFooter(totalUi: ShoppingListDetailsViewModel.TotalUi) {
    Surface(
        shape = RoundedCornerShape(50),
        shadowElevation = 8.dp,
        color = Color(0xFF269900)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total ${totalUi.total.toInt()} kr",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (totalUi.missingCount > 0) {
                Text(
                    text = "* Nogle varer mangler pris",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}


private fun categoryColor(
    category: String,
    theme: ThemeViewModel
): Color =
    when (category.lowercase()) {
        "tørvarer" -> theme.dryGoods
        "kød" -> theme.meat
        "grøntsager" -> theme.vegetables
        "mejeri" -> theme.dairy
        "kolonial" -> theme.kolonial
        else -> theme.other
    }
