package com.example.a62550_foodapp.ui.ShoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.di.getDetailsViewModel
import com.example.a62550_foodapp.model.ShoppingListRowDisplay
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel

@Composable
fun ShoppingListDetailsPage() {
    val viewModel: ShoppingListDetailsViewModel = getDetailsViewModel()
    val items by viewModel.items.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        items(items) { item ->
            ShoppingListRow(item)
        }
    }
}


@Composable
fun ShoppingListRow(item: ShoppingListRowDisplay) {

    val quantityText = remember(item.quantity, item.unit) {
        val qty = if (item.quantity % 1f == 0f) {
            item.quantity.toInt().toString()
        } else {
            item.quantity.toString()
        }

        listOfNotNull(qty, item.unit).joinToString(" ")
    }

    val priceText = remember(item.cheapestPrice) {
        item.cheapestPrice?.let { "%.2f kr".format(it) } ?: "N/A"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = {
                // TODO: call ViewModel to update DB
            }
        )

        Text(
            text = item.itemName,
            modifier = Modifier.weight(1f)
        )

        Text(text = quantityText)

        Text(text = priceText)
    }
}
