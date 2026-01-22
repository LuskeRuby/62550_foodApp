package com.example.a62550_foodapp.ui.shoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a62550_foodapp.db.projection.ShoppingListItemGroupEntry
import com.example.a62550_foodapp.ui.components.SearchSelectField
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.collections.component1
import kotlin.collections.component2
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
public fun AddItemGroupToShoppingListPage(
    shoppingListId: Long,
    disableItemOverlay: () -> Unit = {},
    themeViewModel: ThemeViewModel = koinViewModel(),
    viewModel: ShoppingListDetailsViewModel = koinViewModel(
            key = "ShoppingListDetails-$shoppingListId",
            parameters = { parametersOf(shoppingListId) }
        )
) {
    val itemGroupEntries: List<ShoppingListItemGroupEntry> by viewModel.itemGroupEntries.collectAsState()
    val itemGroups by viewModel.itemGroups.collectAsState()
    val grouped = itemGroupEntries.groupBy { it.category }

    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(themeViewModel.backgroundColor)) {

        // header
        Text(
            "Tilføj til indkøbslisten",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        //TODO handle portion size
        SearchSelectField(
            label = "Search items",
            items = itemGroups,
            itemText = { it.name },
            itemUnit = { it.unitType },

            value = searchText,
            onValueChange = { searchText = it },

            onItemSelected = { entry ->
                viewModel.add(
                    addedItem = entry,
                    portionSize = 0f
                )
                searchText = "" // ryd efter tilføj
            }
        )

        // body
        LazyColumn() {
            grouped.forEach { (category, categoryItems) ->
                item { CategoryHeader(category) }
                items (categoryItems, key = { it.id }) { itemGroupEntry ->
                    EditItemRow(
                        item = itemGroupEntry,
                        onDelete = { viewModel.delete(itemGroupEntry) }
                    )
                }
            }
        }
    }


    // footer
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = disableItemOverlay,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(end = 20.dp, bottom = 20.dp)
                .height(48.dp)
                .width(160.dp),
            shape = RoundedCornerShape(24.dp),
            containerColor = themeViewModel.addButtonColor,
            contentColor = themeViewModel.onPrimaryColor
        ) {
            Text(text = "Færdig")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemRow(
    themeViewModel: ThemeViewModel = koinViewModel(),
    item: ShoppingListItemGroupEntry,
    onDelete: () -> Unit
) {
    //used for swipe-remove
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
        enableDismissFromStartToEnd = false,
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
                .background(themeViewModel.backgroundColor,
                    RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            //name
            Text(
                text = "${item.quantity} x ${item.name}",
                modifier = Modifier.weight(1f),
                fontSize = 15.sp,
                color = themeViewModel.textPrimary,
                fontWeight = FontWeight.Medium
            )

            //size
            Text(
                text = "${item.quantity} × ${item.size.toInt()} ${item.unitType}",
                modifier = Modifier.width(90.dp),
                fontSize = 13.sp,
                color = themeViewModel.textPrimary,
                textAlign = TextAlign.End
            )
        }
    }
}
