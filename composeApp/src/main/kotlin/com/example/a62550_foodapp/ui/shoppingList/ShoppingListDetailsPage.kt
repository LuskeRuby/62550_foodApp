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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a62550_foodapp.db.projection.ItemWithPriceAndCategory
import com.example.a62550_foodapp.model.ShoppingListEntryUi
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.collections.component1
import kotlin.collections.component2

// top layer so we can reuse ShoppingListContent and ShoppingItemRow
@Composable
fun ShoppingListDetailsPage(
    shoppingListId: Int,
) {

    var addItemsOverlay by remember { mutableStateOf(false) }

    if (!addItemsOverlay) {
        ShoppingListPage(
            shoppingListId = shoppingListId,
            onAddItemsButtonClick = { addItemsOverlay = true },
            addItemsOverlay = addItemsOverlay
        )
    } else {
        BackHandler() { addItemsOverlay = false }
        AddItemToShoppingListPage(
            shoppingListId  = shoppingListId,
            addItemsOverlay = addItemsOverlay,
            disableItemOverlay = { addItemsOverlay = false }
        )
    }

}

@Composable
private fun ShoppingListPage(
    shoppingListId: Int,
    onAddItemsButtonClick: () -> Unit = {},
    addItemsOverlay: Boolean,
    themeViewModel: ThemeViewModel = koinViewModel(),
    viewModel: ShoppingListDetailsViewModel =
        koinViewModel(
            key = "ShoppingListDetails-$shoppingListId",
            parameters = { parametersOf(shoppingListId) }
        )
) {
    val items by viewModel.items.collectAsState()
    val totalUi by viewModel.totalUi.collectAsState()
    val selectedSupermarket by viewModel.selectedSupermarketId.collectAsState()

    val grouped = items.groupBy { it.category }

    Box(modifier = Modifier.fillMaxSize().background(themeViewModel.backgroundColor)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // header
            Text(
                "Indkøbsliste",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            SupermarketSelector(
                selectedId = selectedSupermarket,
                onSelect = viewModel::selectSupermarket
            )

            // body
            ShoppingListContent(
                viewModel = viewModel,
                addItemsOverlay = addItemsOverlay,
                grouped = grouped
            )

            // footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TotalFooter(totalUi)
            }
        }

        FloatingActionButton(
            onClick = onAddItemsButtonClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            containerColor = themeViewModel.addButtonColor,
            contentColor = themeViewModel.onPrimaryColor
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Item")
        }
    }
}

@Composable
private fun AddItemToShoppingListPage(
    shoppingListId: Int,
    addItemsOverlay: Boolean,
    disableItemOverlay: () -> Unit = {},
    themeViewModel: ThemeViewModel = koinViewModel(),
    viewModel: ShoppingListDetailsViewModel =
        koinViewModel(
            key = "ShoppingListDetails-$shoppingListId",
            parameters = { parametersOf(shoppingListId) }
        )
) {
    val items by viewModel.tempItemsList.collectAsState()
    val grouped = items.groupBy { it.category }

    Column(modifier = Modifier.fillMaxSize()) {

        // header
        Text(
            "Tilføj til indkøbslisten",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        ItemSearchField(
            onAddItemsToTempList = { entry: ItemWithPriceAndCategory -> viewModel.addTempItem(entry)}
        )

        // body
        ShoppingListContent(
            viewModel = viewModel,
            addItemsOverlay = addItemsOverlay,
            grouped = grouped
        )

        // footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                onClick = {
                    viewModel.addItem(items)
                    viewModel.clearTempItems()
                    disableItemOverlay()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF269900),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Tilføj")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    viewModel.clearTempItems()
                    disableItemOverlay()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Fortryd")
            }
        }
    }
}

@Composable
private fun ShoppingListContent(
    viewModel: ShoppingListDetailsViewModel,
    addItemsOverlay: Boolean,
    grouped: Map<String, List<ShoppingListEntryUi>>
){
    LazyColumn() {
        grouped.forEach { (category, categoryItems) ->
            item { CategoryHeader(category) }
            items(categoryItems, key = { it.itemId }) { item ->
                ShoppingItemRow(
                    item = item,
                    onCheckedChange =
                        if (!addItemsOverlay) {
                            { checked: Boolean -> viewModel.setChecked(item.itemId, checked) }
                        } else {
                            // disable when checkbox not visible
                            { _: Boolean -> }
                        },
                    checkboxVisible = !addItemsOverlay,
                    onDelete =
                        if (!addItemsOverlay) {
                            { viewModel.deleteItem(item.itemId) }
                        } else {
                            { viewModel.removeTempItem(item.itemId)}
                        },
                    modifier = Modifier.animateItem()
                )

            }
        }
    }
}

@Composable
private fun SupermarketSelector(
    selectedId: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedId == 1,
            onClick = { onSelect(1) },
            label = { Text("Netto") }
        )
        FilterChip(
            selected = selectedId == 2,
            onClick = { onSelect(2) },
            label = { Text("Kvickly") }
        )
    }
}
@Composable
fun categoryColor(
    category: String,
    themeViewModel: ThemeViewModel = koinViewModel()
): Color =
    when (category.lowercase()) {
        "tørvarer" -> themeViewModel.dryGoods
        "kød" -> themeViewModel.meat
        "grøntsager" -> themeViewModel.vegetables
        "mejeri" -> themeViewModel.dairy
        "kolonial" -> themeViewModel.kolonial
        else -> themeViewModel.other
    }

@Composable
private fun CategoryHeader(category: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(categoryColor(category))
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
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = koinViewModel(),
    item: ShoppingListEntryUi,
    onCheckedChange: (Boolean) -> Unit,
    checkboxVisible: Boolean,
    onDelete: () -> Unit
) {
    //used for swiperemove
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { fullWidth ->
            fullWidth * 0.4f   // //how much swipe before delete (40% her)
        },
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete item",
                    tint = Color.White
                )
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (item.isChecked) themeViewModel.fadedBackground
                    else themeViewModel.backgroundColor,
                    RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            //name
            CheckboxText(
                checked = item.isChecked,
                text = "${item.quantity} x ${item.name}",
                modifier = Modifier.weight(1f),
                fontSize = 15.sp,
                color = themeViewModel.textPrimary,
                fontWeight = FontWeight.Medium
            )

            //size
            CheckboxText(
                checked = item.isChecked,
                text = "${item.quantity} × ${item.size.toInt()} ${item.unitType}",
                modifier = Modifier.width(90.dp),
                fontSize = 13.sp,
                color = themeViewModel.textPrimary,
                textAlign = TextAlign.End
            )

            //price
            val qty = item.quantity
            val unitPrice = item.price

            Column(
                modifier = Modifier.width(80.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (unitPrice != null) {
                    if (qty > 1) {
                        CheckboxText(
                            checked = item.isChecked,
                            text = "${item.quantity} x ${unitPrice.toInt()} kr",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.End
                        )
                        CheckboxText(
                            checked = item.isChecked,
                            text = "${(unitPrice * qty).toInt()} kr",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeViewModel.priceTagColor
                        )
                    } else {
                        CheckboxText(
                            checked = item.isChecked,
                            text = "${unitPrice.toInt()} kr",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = themeViewModel.priceTagColor
                        )
                    }
                }
                //fallback
                else {
                    CheckboxText(
                        checked = item.isChecked,
                        text = "Utilgængelig",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = themeViewModel.priceTagColor
                    )
                }
            }

            if (checkboxVisible) {
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
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text =
                        "Total ${totalUi.total.toInt()} kr",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (totalUi.missingCount > 0) {
                Text(
                    text = "* Nogle varer mangler pris",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

