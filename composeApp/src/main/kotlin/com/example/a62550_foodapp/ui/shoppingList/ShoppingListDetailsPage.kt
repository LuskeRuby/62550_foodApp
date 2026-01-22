package com.example.a62550_foodapp.ui.shoppingList

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.a62550_foodapp.db.projection.ShoppingListEntry
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/** Data class to group items by supermarket with both name and logo */
private data class SupermarketGroup(val name: String, val logo: String?)

@Composable
fun ShoppingListDetailsPage(
    shoppingListId: Long,
) {
    var addItemsOverlay by remember { mutableStateOf(false) }

    if (!addItemsOverlay) {
        ShoppingListPage(
            shoppingListId = shoppingListId,
            onAddItemsButtonClick = { addItemsOverlay = true },
            editOverlay = false
        )
    } else {
        BackHandler { addItemsOverlay = false }
        AddItemGroupToShoppingListPage(
            shoppingListId = shoppingListId,
            disableItemOverlay = { addItemsOverlay = false }
        )
    }
}


@Composable
private fun ShoppingListPage(
    shoppingListId: Long,
    onAddItemsButtonClick: () -> Unit,
    editOverlay: Boolean,
    themeViewModel: ThemeViewModel = koinViewModel(),
    viewModel: ShoppingListDetailsViewModel =
        koinViewModel(
            key = "ShoppingListDetails-$shoppingListId",
            parameters = { parametersOf(shoppingListId) }
        )
) {
    val items by viewModel.items.collectAsState()
    val totalUi by viewModel.shoppingListTotalPrice.collectAsState()

    val storeFilterViewModel: StoreFilterViewModel = koinViewModel()
    val selectedStores by storeFilterViewModel.selectedStores.collectAsState()

    LaunchedEffect(selectedStores) {
        viewModel.setStoreFilter(selectedStores)
    }

    // Group by supermarket, capturing both name and logo
    val grouped =
        items
            .groupBy { SupermarketGroup(it.superMarketName ?: "", it.superMarketLogo) }
            .mapValues { (_, categoryItems) ->
                categoryItems.groupBy { it.category }
            }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            /* Header */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Indkøbsliste",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                TotalBox(totalUi)
            }

            /* List */
            ShoppingListContent(
                viewModel = viewModel,
                editOverlay = editOverlay,
                grouped = grouped
            )
        }

        FloatingActionButton(
            onClick = onAddItemsButtonClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(end = 20.dp, bottom = 20.dp)
                .height(48.dp)
                .width(160.dp),
            shape = RoundedCornerShape(24.dp),
            containerColor = themeViewModel.addButtonColor,
            contentColor = themeViewModel.onPrimaryColor
        ) {
            Text("Tilføj varer")
        }
    }
}

@Composable
private fun ShoppingListContent(
    viewModel: ShoppingListDetailsViewModel,
    editOverlay: Boolean,
    grouped: Map<SupermarketGroup, Map<String, List<ShoppingListEntry>>>
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        grouped.forEach { (supermarket, categoryMap) ->

            if (supermarket.name.isNotBlank()) {
                item { SuperMarketHeader(name = supermarket.name, logo = supermarket.logo) }
            }

            categoryMap.forEach { (category, items) ->
                item { CategoryHeader(category) }

                items(
                    items = items,
                    key = { "${it.id}-${it.itemGroupId}-${it.superMarketName}" }
                ) { entry ->
                    ShoppingItemRow(
                        item = entry,
                        onCheckedChange =
                            if (!editOverlay) {
                                { checked -> viewModel.setCheckmark(entry, checked) }
                            } else {
                                {}
                            },
                        editList = !editOverlay,
                        onDelete = { viewModel.delete(entry) },
                        supermarketName = supermarket.name
                    )
                }
            }
        }
    }
}

@Composable
private fun SuperMarketHeader(
    name: String,
    logo: String?,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    // Determine color based on supermarket name
    val backgroundColor = when (name.lowercase()) {
        "netto" -> Color(0xFFFFCC00)           // Yellow (Netto brand color)
        "kvickly" -> Color(0xFFFF1E31)         // Bright Red (Kvickly brand color)
        "føtex" -> Color(0xFF27AE60)           // Green (Føtex brand color)
        "meny" -> Color(0xFF86180C)            // Darker Red (Meny brand color)
        "bilka" -> Color(0xFF3B8DDC)           // Darker blue (Bilka brand color)
        "rema 1000" -> Color(0xFF6B95B6)       // Light blue (Rema 1000 brand color)
        else -> Color(0xFF252525)              // Black fallback
    }

    // Logos that need white background behind them for visibility
    val needsWhiteBackground = when (name.lowercase()) {
        "kvickly", "bilka" -> true
        else -> false
    }

    val textColor = when (name.lowercase()) {
        "netto" -> Color.Black                 // Dark text on yellow
        else -> Color.White                    // White text on dark backgrounds
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (logo != null) {
            // Add white background for logos that need it (dark text on logo)
            Box(
                modifier = if (needsWhiteBackground) {
                    Modifier
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                } else Modifier,
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = logo,
                    contentDescription = name,
                    modifier = Modifier
                        .height(50.dp)
                        .widthIn(max = 180.dp),
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            Text(
                text = name,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
    }
}

//TODO add more/all categories
@Composable
fun CategoryHeader(category: String) {
    val themeViewModel: ThemeViewModel = koinViewModel()
    val color = when (category.lowercase()) {
        "tørvarer" -> themeViewModel.dryGoods
        "kød" -> themeViewModel.meat
        "fisk" -> themeViewModel.fish
        "grøntsager" -> themeViewModel.vegetables
        "mejeri" -> themeViewModel.dairy
        "krydderier" -> themeViewModel.spices
        "kolonial" -> themeViewModel.kolonial
        "brød" -> themeViewModel.bread
        "utilgængelige varer" -> themeViewModel.unavailable
        else -> themeViewModel.other
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Text(
            text = category,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingItemRow(
    themeViewModel: ThemeViewModel = koinViewModel(),
    item: ShoppingListEntry,
    onCheckedChange: (Boolean) -> Unit,
    editList: Boolean,
    onDelete: () -> Unit,
    supermarketName: String = ""
) {
    // Get supermarket brand color for the left border accent
    val supermarketColor = when (supermarketName.lowercase()) {
        "netto" -> Color(0xFFFFCC00)           // Yellow
        "kvickly" -> Color(0xFFFF1E31)         // Red
        "føtex" -> Color(0xFF27AE60)           // Green
        "meny" -> Color(0xFF86180C)            // Dark Red
        "bilka" -> Color(0xFF3B8DDC)           // Blue
        "rema 1000" -> Color(0xFF6B95B6)       // Light blue
        else -> Color.Transparent
    }

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.4f },
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
                    RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                )
                .then(
                    if (supermarketColor != Color.Transparent) {
                        Modifier.border(
                            width = 3.dp,
                            color = supermarketColor,
                            shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                        )
                    } else Modifier
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            CheckboxText(
                checked = item.isChecked,
                text = "${item.quantity} x ${item.itemName}",
                modifier = Modifier.weight(1f),
                fontSize = 15.sp,
                color = themeViewModel.textPrimary,
                fontWeight = FontWeight.Medium
            )
            val sizeText =
                item.size?.let { "${it.toInt()} ${item.unitType}" }
                    ?: item.unitType

            CheckboxText(
                checked = item.isChecked,
                text = "${item.quantity} × $sizeText",
                modifier = Modifier.width(90.dp),
                fontSize = 13.sp,
                color = themeViewModel.textPrimary,
                textAlign = TextAlign.End
            )


            Column(
                modifier = Modifier.width(80.dp),
                horizontalAlignment = Alignment.End
            ) {
                item.price?.let { price ->
                    CheckboxText(
                        checked = item.isChecked,
                        text = "${(price * item.quantity).toInt()} kr",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeViewModel.priceTagColor
                    )
                } ?: CheckboxText(
                    checked = item.isChecked,
                    text = "Utilgængelig",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = themeViewModel.priceTagColor
                )
            }

            if (editList) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }
}


@Composable
private fun CheckboxText(
    checked: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    color: Color,
    fontSize: TextUnit,
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
private fun TotalBox(totalUi: ShoppingListDetailsViewModel.TotalUi) {
    val themeViewModel: ThemeViewModel = koinViewModel()

    Surface(
        shape = RoundedCornerShape(50),
        shadowElevation = 8.dp,
        color = themeViewModel.totalPriceColor
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Total ${totalUi.total.toInt()} kr",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (totalUi.missingCount > 0) {
                Text(
                    "* Nogle varer mangler pris",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}
