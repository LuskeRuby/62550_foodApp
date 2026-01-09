package com.example.a62550_foodapp.ui.shoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.di.getDetailsViewModel
import com.example.a62550_foodapp.viewmodel.ItemUi
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel

@Composable
fun ShoppingListDetailsPage() {
    val viewModel: ShoppingListDetailsViewModel = getDetailsViewModel()
    val supermarkets by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 12.dp)
    ) {
        supermarkets.forEach { supermarket ->

            //Supermarket
            item {
                SupermarketHeader(
                    name = supermarket.name,
                    total = supermarket.totalPrice
                )
            }

            supermarket.categories.forEach { category ->

                //category
                item {
                    CategoryHeader(category.groupId)
                }

                //items
                items(category.items) { item ->
                    ShoppingItemRow(
                        item = item,
                        onCheckedChange = {
                            viewModel.onCheckedChange(item.itemId, it)
                        }
                    )
                }
            }

            // Spacer between supermarkets
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SupermarketHeader(
    name: String,
    total: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFEFEF))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Text(
            text = name,
            color = Color.Black
        )
        Text(
            text = "Total: ${total.toInt()} kr",
            color = Color.DarkGray
        )
    }
}

@Composable
fun CategoryHeader(groupId: Int?) {
    val title = when (groupId) {
        1 -> "Frugt & Grønt"
        2 -> "Mejeri"
        3 -> "Basisvarer"
        else -> "Andet"
    }

    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDDDDDD))
            .padding(vertical = 4.dp, horizontal = 12.dp),
        color = Color.Black
    )
}


@Composable
fun ShoppingItemRow(
    item: ItemUi,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Item name (left, takes most space)
        Text(
            text = item.name,
            modifier = Modifier.weight(1.6f),
            color = Color.Black
        )

        // Quantity + unit (middle column)
        Text(
            text = formatQuantityWithUnit(item.quantity, item.unit),
            modifier = Modifier.weight(1.0f),
            color = Color.DarkGray
        )

        // Price (right column, aligned right)
        Text(
            text = "${item.price.toInt()} kr",
            modifier = Modifier
                .weight(0.8f),
            textAlign = TextAlign.End,
            color = Color.Black
        )

        // Checkbox
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

fun formatQuantityWithUnit(
    quantity: Float,
    unit: String?
): String {
    val qtyText = if (quantity % 1f == 0f) {
        quantity.toInt().toString()
    } else {
        quantity.toString()
    }

    return when {
        quantity == 1f && unit != null ->
            unit.replaceFirstChar { it.uppercase() }

        unit == null ->
            qtyText

        else ->
            "$qtyText × $unit"
    }
}

