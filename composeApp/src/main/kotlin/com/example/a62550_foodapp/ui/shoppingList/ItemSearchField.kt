package com.example.a62550_foodapp.ui.shoppingList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.db.projection.ItemWithPriceAndCategory
import com.example.a62550_foodapp.viewmodel.ItemViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ItemSearchField(
    onAddItemsToTempList: (ItemWithPriceAndCategory) -> Unit,
    itemViewModel: ItemViewModel = koinViewModel()
) {
    // for selecting existing items
    val dbItems by itemViewModel.items.collectAsState()

    // search state replaces the previous dropdown/expanded UI
    var searchQuery by remember { mutableStateOf("") }
    var selectedItemId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            if (dbItems.isEmpty()) {
                Text("No items available.")
            } else {
                // Compute matches and the unit type to display
                val matches = if (searchQuery.isNotBlank()) dbItems.filter { it.item.name.contains(searchQuery, ignoreCase = true) } else emptyList()
                val displayedUnitType = when {
                    selectedItemId != null -> dbItems.firstOrNull { it.item.id == selectedItemId }?.item?.unitType
                    matches.isNotEmpty() -> matches.first().item.unitType
                    else -> null
                }

                // Row with search field
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search items") },
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = displayedUnitType ?: "",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Suggestions shown when user typed something
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
                                                onAddItemsToTempList(item)
                                            }
                                            .padding(8.dp)
                                    ) {
                                        Text(item.item.name)
                                    }
                                    if (index != matches.lastIndex) {
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
