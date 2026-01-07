package com.example.a62550_foodapp.ui.ShoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


data class ShoppingItem1(
    val id: Int,
    val name: String
)


@Composable
fun ShoppingListPage() {
    val items = listOf(
        ShoppingItem1(1, "Milk"),
        ShoppingItem1(2, "Bread"),
        ShoppingItem1(3, "Eggs")
    )
    Column(Modifier
        .fillMaxSize()
        .background(Color.White).padding(16.dp)) {
            LazyColumn() {
                items(items) { item ->
                    ShoppingListRow(item)

            }
        }
    }
}

@Composable
fun ShoppingListRow(item: ShoppingItem1) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp,)
            .background(Color.Red,shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = item.name)

            Icon(
                Icons.Default.Fastfood,
                contentDescription = "Food",

            )
        }

    }

}
