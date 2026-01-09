package com.example.a62550_foodapp.ui.shoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.di.getShoppingListDetailsViewModel

@Composable
fun ShoppingListDetailsPage(
    shoppingListId: Int
) {
    val viewModel = getShoppingListDetailsViewModel(shoppingListId)
    val items by viewModel.items.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        items(
            items = items,
            key = { it.itemId }
        ) { item ->
            Text(
                text = item.name,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
