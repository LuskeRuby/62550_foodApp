package com.example.a62550_foodapp.ui.shoppingList

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun ShoppingListDetailsPage(
    shoppingListId: Long,
    onBack: () -> Unit
) {
    var addItemsOverlay by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource()}) {} // to prevent clicking underlying buttons
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
            AddItemGroupToShoppingListPage(
                shoppingListId = shoppingListId,
                disableItemOverlay = { addItemsOverlay = false }
            )
        }
    }

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


    val grouped =
        items
            .groupBy { it.superMarketName ?: "" }
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
    grouped: Map<String, Map<String, List<ShoppingListEntry>>>
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
                        onCheckedChange =
                            if (!editOverlay) {
                                { checked -> viewModel.setCheckmark(entry, checked) }
                            } else {
                                {}
                            },
                        editList = !editOverlay,
                        onDelete = { viewModel.delete(entry) }
                    )
                }
            }
        }
    }
}

//TODO make it look sexy
@Composable
private fun SuperMarketHeader(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF6200EE))
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Text(
            text = name,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

//TODO add more/all categories
@Composable
fun CategoryHeader(category: String) {
    val themeViewModel: ThemeViewModel = koinViewModel()
    val color = when (category.lowercase()) {
        "tørvarer" -> themeViewModel.dryGoods
        "kød" -> themeViewModel.meat
        "grøntsager" -> themeViewModel.vegetables
        "mejeri" -> themeViewModel.dairy
        "kolonial" -> themeViewModel.kolonial
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
    onDelete: () -> Unit
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
                    text = "*",
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
                    "* mangler pris",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}
