package com.example.a62550_foodapp.ui.shoppingList

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ShoppingListRow(
    item: ShoppingListEntryUi,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange
        )

        Spacer(Modifier.width(12.dp))

        item.imagePath?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(12.dp))
        }

        Column {
            Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${item.quantity} × ${item.size} ${item.unitType}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
