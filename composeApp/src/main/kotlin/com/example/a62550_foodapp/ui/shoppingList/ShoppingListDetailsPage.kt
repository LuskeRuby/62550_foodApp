package com.example.a62550_foodapp.ui.shoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ShoppingListDetailsPage(
    shoppingListId: Int
) {
    val viewModel: ShoppingListDetailsViewModel =
        koinViewModel(
            key = "ShoppingListDetails-$shoppingListId",
            parameters = { parametersOf(shoppingListId) }
        )

    val items by viewModel.items.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        //header
        Text(
            text = "Shopping list",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
        )

        //end of header
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    EmptyState()
                }
            }

            items(items, key = { it.itemId }) { item ->
                ShoppingListItemCard(
                    name = item.name,
                    quantity = item.quantity,
                    unitType = item.unitType,
                    isChecked = item.isChecked,
                    category = item.category,
                    onCheckedChange = { checked ->
                        viewModel.setChecked(item.itemId, checked)
                    }
                )
            }
        }
    }
}

@Composable
private fun ShoppingListItemCard(
    name: String,
    quantity: Float,
    unitType: String,
    isChecked: Boolean,
    category: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            //image placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            //text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${quantity.toInt()} $unitType • $category",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }

            /* --- CHECKBOX --- */
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No items yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add items or recipes to this list",
            color = Color.Gray
        )
    }
}
