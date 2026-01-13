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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun ShoppingListDetailsPage(
    shoppingListId: Int
) {
    val viewModel: ShoppingListDetailsViewModel =
        koinViewModel(
            key = "ShoppingListDetails-$shoppingListId",
            parameters = { parametersOf(shoppingListId) }
        )

    var addItemsOverlay by remember { mutableStateOf(false) }

    if (!addItemsOverlay) {
        ViewShoppingList(
            viewModel = viewModel,
            onAddItemsButtonClick = { addItemsOverlay = true },
            addItemsOverlay = addItemsOverlay
        )
    } else {
        BackHandler() { addItemsOverlay = false }
        AddShoppingListItems(
            viewModel = viewModel,
            addItemsOverlay = addItemsOverlay
        )
    }

}

@Composable
private fun ViewShoppingList(
    viewModel: ShoppingListDetailsViewModel,
    onAddItemsButtonClick: () -> Unit = {},
    addItemsOverlay: Boolean
) {
    val items by viewModel.items.collectAsState()
    val total by viewModel.totalPrice.collectAsState()
    val selectedSupermarket by viewModel.selectedSupermarketId.collectAsState()

    val grouped = items.groupBy { it.category }

    Column(Modifier.fillMaxSize().background(Color.White)) {

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
            grouped = grouped,
            viewModel = viewModel,
            addItemsOverlay = addItemsOverlay
        )

        // footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AddItemButton(onClick = onAddItemsButtonClick)
            TotalFooter(total)
        }

    }
}

@Composable
private fun AddShoppingListItems(
    viewModel: ShoppingListDetailsViewModel,
    addItemsOverlay: Boolean
) {
    val items by viewModel.addItemsList.collectAsState()
    val grouped = items.groupBy { it.category }

    // header
    Text(
        "Tilføj til indkøbslisten",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )

    // body

    ShoppingListContent(
        grouped = grouped,
        viewModel = viewModel,
        addItemsOverlay = addItemsOverlay
    )

    // footer



}

@Composable
private fun ShoppingListContent(
    grouped: Map<String, List<ShoppingListEntryUi>>,
    viewModel: ShoppingListDetailsViewModel,
    addItemsOverlay: Boolean
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
                            // disable when delete not available
                            { }
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

fun categoryColor(category: String): Color =
    when (category.lowercase()) {
        "tørvarer" -> Color(0xFF996600)
        "kød" -> Color(0xFFD32F2F)
        "grøntsager" -> Color(0xFF388E3C)
        "mejeri" -> Color(0xFF1976D2)
        "kolonial" -> Color(0xFF6A1B9A)
        else -> Color(0xFF757575)
    }
@Composable
fun CategoryHeader(category: String) {
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
    item: ShoppingListEntryUi,
    onCheckedChange: (Boolean) -> Unit,
    checkboxVisible: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    //used for swiperemove
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { fullWidth ->
            fullWidth * 0.6f   // how much swipe % before delete
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
                contentAlignment = Alignment.CenterEnd
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
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            //name
            Text(
                text = "${item.quantity.toInt()} x ${item.name}",
                modifier = Modifier.weight(1f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            //size
            Text(
                text = "${item.quantity.toInt()} × ${item.size.toInt()} ${item.unitType}",
                modifier = Modifier.width(90.dp),
                fontSize = 13.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.End
            )

            //price
            val qty = item.quantity.toInt()
            val unitPrice = item.price

            Column(
                modifier = Modifier.width(80.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (unitPrice != null) {
                    if (qty > 1) {
                        Text(
                            text = "${item.quantity.toInt()} x ${unitPrice.toInt()} kr",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${(unitPrice * qty).toInt()} kr",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "${unitPrice.toInt()} kr",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "-",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            if (checkboxVisible) {
                //checkbox
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }
}
@Composable
private fun TotalFooter(total: Float?) {
    Surface(
        shape = RoundedCornerShape(50),
        shadowElevation = 8.dp,
        color = Color(0xFF269900)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = total?.let { "${it.toInt()} kr" } ?: "-",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AddItemButton(onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xfffff5cc)),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "+",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
