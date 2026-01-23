package com.example.a62550_foodapp.ui.shoppingList

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.db.projection.ShoppingListEntry
import com.example.a62550_foodapp.db.projection.ShoppingListItemGroupEntry
import com.example.a62550_foodapp.ui.components.AddItemGroupPage
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
    onBack: () -> Unit
) {
    var addItemsOverlay by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // to prevent clicking underlying buttons
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource()}) {}
    ) {
        if (!addItemsOverlay) {
            ShoppingListPage(
                onBack = onBack,
                shoppingListId = shoppingListId,
                onAddItemsButtonClick = { addItemsOverlay = true },
                editOverlay = false
            )
        } else {
            BackHandler { addItemsOverlay = false }

            // Reuse same VM instance as the details page (same key + params)
            val viewModel: ShoppingListDetailsViewModel = koinViewModel(
                key = "ShoppingListDetails-$shoppingListId",
                parameters = { parametersOf(shoppingListId) }
            )

            val allGroups by viewModel.itemGroups.collectAsState()
            val itemGroupEntries by viewModel.itemGroupEntries.collectAsState()

            //  merge duplicates per group.
            val selectedItems: List<Pair<ItemGroup, Int>> = remember(allGroups, itemGroupEntries) {
                buildSelectedItems(allGroups, itemGroupEntries)
            }

            AddItemGroupPage(
                headerText = "Tilføj til indkøbslisten",
                allGroups = allGroups,
                selectedItems = selectedItems,
                onAdd = { group, qty ->
                    // Shopping list stores qty as portionSize (Float)
                    viewModel.add(
                        addedItem = group,
                        portionSize = qty.toFloat()
                    )
                },
                onDelete = { group ->
                    // name + unitType.
                    itemGroupEntries
                        .filter { it.name == group.name && it.unitType == group.unitType }
                        .forEach { viewModel.delete(it) }
                },
                onDone = { addItemsOverlay = false }
            )
        }
    }

}

// Shared AddItemGroupPage expects (ItemGroup, Int)
private fun buildSelectedItems(
    allGroups: List<ItemGroup>,
    itemGroupEntries: List<ShoppingListItemGroupEntry>
): List<Pair<ItemGroup, Int>> {
    if (allGroups.isEmpty() || itemGroupEntries.isEmpty()) return emptyList()

    // We merge by the same key used in the previous ShoppingList add-items UI.
    val groupByNameAndUnit: Map<Pair<String, String>, ItemGroup> =
        allGroups.associateBy { it.name to it.unitType }

    return itemGroupEntries
        .groupBy { it.name to it.unitType }
        .mapNotNull { (key, entries) ->
            val group = groupByNameAndUnit[key] ?: return@mapNotNull null

            // We pproximate by summing sizes and truncating.
            val qty = entries.sumOf { it.size.toDouble() }.toInt().coerceAtLeast(0)

            group to qty
        }
        .sortedBy { it.first.name.lowercase() }
}

@Composable
private fun ShoppingListPage(
    onBack: () -> Unit,
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
                    .background(color = themeViewModel.surfaceColor)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

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
    grouped: Map<SupermarketGroup, Map<String, List<ShoppingListEntry>>>,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        grouped.forEach { (supermarket, categoryMap) ->

            // Determine color based on supermarket name
            val supermarketColor = when (supermarket.name.lowercase()) {
                "netto" -> themeViewModel.nettoColor
                "kvickly" -> themeViewModel.kvicklyColor
                "føtex" -> themeViewModel.fotexColor
                "meny" -> themeViewModel.menyColor
                "bilka" -> themeViewModel.bilkaColor
                "rema 1000" -> themeViewModel.rema1000Color
                else -> themeViewModel.otherSuperMarkets
            }

            val cornerRounding = 32.dp
            val bottomPadding = 8.dp

            item {
                SuperMarketHeader(
                    name = supermarket.name,
                    logo = supermarket.logo,
                    backgroundColor = supermarketColor,
                    cornerRounding = cornerRounding
                )
            }


            item {
                //Surface(
                //    modifier = Modifier
                //        .fillMaxWidth(),
                //    color = supermarketColor,
                //    shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp),
                //    tonalElevation = 0.dp
                //){
                //    Column(modifier = Modifier
                //        // for some reason this padding decides the area that gets colored from the surface
                //        .padding(start = 8.dp, end = 8.dp, bottom = 48.dp)
                //    ) {
                        categoryMap.toList().forEachIndexed { categoryIndex, (category, itemList) ->
                            CategoryHeader(category)

                            itemList.forEachIndexed { shoppingListIndex, entry ->

                                val isLastSupermarketItem = categoryIndex == categoryMap.toList().lastIndex &&
                                                            shoppingListIndex == itemList.lastIndex

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
                                    extraPadding = bottomPadding,
                                    customShape =
                                        if (isLastSupermarketItem) RoundedCornerShape(bottomStart = cornerRounding, bottomEnd = cornerRounding)
                                        else RectangleShape
                                )
                            }
                        //}
                    //}
                }
            }
            // space between supermarkets
            item{
                Spacer(modifier = Modifier.padding(12.dp))
            }
        }
    }
}

@Composable
private fun SuperMarketHeader(
    name: String,
    logo: String?,
    backgroundColor: Color,
    themeViewModel: ThemeViewModel = koinViewModel(),
    cornerRounding: Dp = 0.dp
) {

    // Logos that need white background behind them for visibility
    val needsWhiteBackground =
        when (name.lowercase()) {
        "kvickly", "bilka" -> true
        else -> false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(topStart = cornerRounding, topEnd = cornerRounding)
            )
            .padding(start = 12.dp,end = 12.dp, top = 16.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Add white background for logos that need it (dark text on logo)
        Box(
            modifier = if (needsWhiteBackground) {
                Modifier
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            } else Modifier,
            contentAlignment = Alignment.Center
        ) {
            if (logo != null && name.isNotBlank()) {
                AsyncImage(
                    model = logo,
                    contentDescription = name,
                    modifier = Modifier
                        .height(50.dp)
                        .widthIn(max = 180.dp),
                    contentScale = ContentScale.Fit
                )
            } else if ( name.isNotBlank() ) {
                Text(
                    text = name,
                    color = themeViewModel.textOtherSuperMarkets,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            } else {
                Text(
                    text = "Utilgængelige varer",
                    color = themeViewModel.textOtherSuperMarkets,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }
        }
    }
}

@Composable
fun CategoryHeader(
    category: String,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val color =
        when (category.lowercase()) {
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
    item: ShoppingListEntry,
    onCheckedChange: (Boolean) -> Unit,
    editList: Boolean,
    onDelete: () -> Unit,
    extraPadding: Dp = 0.dp,
    customShape: Shape,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
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
                    .shadow(elevation = 20.dp)
                    .fillMaxSize()
                    .background(
                        Color.Red,
                        shape = customShape)
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
                    else themeViewModel.surfaceColor,
                    shape = customShape)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .padding(bottom = extraPadding),
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
                    text = "- kr",
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
                    "mangler priser",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}
