package com.example.a62550_foodapp.ui.shoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.example.a62550_foodapp.db.entity.ItemGroup
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ButtonDefaults

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
    val mergedItems = itemGroupEntries
        .groupBy { it.name to it.unitType }
        .map { (_, items) ->
            val first = items.first()

            first.copy(
                quantity = items.sumOf { it.quantity },                // hvis Int
                size = items.sumOf { it.size.toDouble() }.toFloat()   // hvis Float
            )
        }

    val itemGroups by viewModel.itemGroups.collectAsState()
    val grouped = itemGroupEntries.groupBy { it.category }

    var searchText by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<ItemGroup?>(null) }
    var qtyText by remember { mutableStateOf("") }

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

            value = searchText,
            onValueChange = { searchText = it },

            onItemSelected = { entry ->
                selectedGroup = entry
            }
        )

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            OutlinedTextField(
                value = qtyText,
                onValueChange = { qtyText = it.filter(Char::isDigit) },
                label = { Text("Antal") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = selectedGroup?.unitType ?: "",
                onValueChange = {},
                label = { Text("Type") },
                enabled = false,
                modifier = Modifier.width(90.dp)
            )

            // -------- ADD --------
            val canAdd = selectedGroup != null && qtyText.isNotBlank()

            Button(
                enabled = canAdd,
                onClick = {
                    val qty = qtyText.toFloatOrNull() ?: return@Button

                    selectedGroup?.let {
                        viewModel.add(
                            addedItem = it,
                            portionSize = qty
                        )
                    }

                    qtyText = ""
                    selectedGroup = null
                    searchText = ""
                },
                modifier = Modifier.height(48.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAdd)
                        themeViewModel.addButtonColor
                    else
                        Color.LightGray,
                    contentColor = themeViewModel.onPrimaryColor,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = themeViewModel.onPrimaryColor.copy(alpha = 0.6f)
                )
            ) {
                Text("Tilføj")
            }

        }

        // body
        LazyColumn(
            modifier = Modifier.padding(top = 12.dp)
        ) {
            items(
                mergedItems,
                key = { it.name + it.unitType }
            ) { itemGroupEntry ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                ) {
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(themeViewModel.backgroundColor)
            .clip(RoundedCornerShape(12.dp))
    ) {
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
                    .background(themeViewModel.backgroundColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
}
