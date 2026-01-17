package com.example.a62550_foodapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> SearchSelectField(
    label: String,
    items: List<T>,
    itemText: (T) -> String,
    itemUnit: (T) -> String? = { null },
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedItem: T? by remember { mutableStateOf(null) }

    val matches =
        if (searchQuery.isNotBlank())
            items.filter { itemText(it).contains(searchQuery, ignoreCase = true) }
        else emptyList()

    val displayedUnitType = when {
        selectedItem != null -> itemUnit(selectedItem!!)
        matches.isNotEmpty() -> itemUnit(matches.first())
        else -> null
    }

    Column(modifier = modifier) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    selectedItem = null
                },
                label = { Text(label) },
                modifier = Modifier.weight(1f)
            )

            Text(displayedUnitType ?: "")
        }

        Spacer(Modifier.height(8.dp))

        if (searchQuery.isNotBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(4.dp)) {
                    if (matches.isEmpty()) {
                        Text("No matches", modifier = Modifier.padding(8.dp))
                    } else {
                        matches.take(8).forEachIndexed { index, item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemSelected(item)
                                        selectedItem = null
                                        searchQuery = ""
                                    }
                                    .padding(8.dp)
                            ) {
                                Text(itemText(item))
                            }
                            if (index != matches.lastIndex) HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

