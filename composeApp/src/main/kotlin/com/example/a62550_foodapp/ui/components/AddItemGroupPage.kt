package com.example.a62550_foodapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete


@Composable
fun AddItemGroupPage(
    headerText: String,

    allGroups: List<ItemGroup>,
    selectedItems: List<Pair<ItemGroup, Int>>, // (group, qty)

    onAdd: (ItemGroup, Int) -> Unit,
    onDelete: (ItemGroup) -> Unit,
    onDone: () -> Unit,

    themeViewModel: ThemeViewModel = koinViewModel()
) {
    var searchText by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<ItemGroup?>(null) }
    var qtyText by remember { mutableStateOf("") }
    val qtyFocusRequester = remember { FocusRequester() }

    LaunchedEffect(selectedGroup) {
        if (selectedGroup != null) qtyFocusRequester.requestFocus()
    }

    Box(Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeViewModel.backgroundColor)
        ) {

            // ===== WHITE HEADER =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeViewModel.surfaceColor)
            ) {
                Column {

                    Text(
                        headerText,
                        fontSize = 18.sp, // samme som resten
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp)
                    ) {

                        val rowHeight = TextFieldDefaults.MinHeight

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(rowHeight)
                        ) {
                            SearchSelectField(
                                label = "Søg vare",
                                items = allGroups,
                                itemText = { it.name },
                                value = searchText,
                                onValueChange = { searchText = it },
                                onItemSelected = { selectedGroup = it },
                                borderColor = themeViewModel.addButtonColor
                            )
                        }

                        OutlinedTextField(
                            value = qtyText,
                            onValueChange = { qtyText = it.filter(Char::isDigit) },
                            modifier = Modifier
                                .width(110.dp)
                                .height(rowHeight)
                                .focusRequester(qtyFocusRequester),
                            singleLine = true,
                            placeholder = { Text("") },
                            trailingIcon = {
                                Text(
                                    text = selectedGroup?.unitType ?: "kg/L",
                                    fontSize = 12.sp,
                                    color = themeViewModel.textSecondary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeViewModel.addButtonColor,
                                unfocusedBorderColor = themeViewModel.addButtonColor,
                                cursorColor = themeViewModel.addButtonColor
                            )
                        )

                        Button(
                            onClick = {
                                val qty = qtyText.toIntOrNull() ?: return@Button
                                val group = selectedGroup ?: return@Button

                                onAdd(group, qty)

                                qtyText = ""
                                selectedGroup = null
                                searchText = ""
                            },
                            modifier = Modifier.height(rowHeight),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeViewModel.addButtonColor,
                                contentColor = themeViewModel.onPrimaryColor
                            )
                        ) {
                            Text("Tilføj")
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ===== LIST CARD =====
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = themeViewModel.surfaceColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                LazyColumn {
                    items(selectedItems, key = { it.first.id }) { (group, qty) ->

                        SwipeIngredientRow(
                            name = group.name,
                            quantity = qty,
                            unit = group.unitType,
                            onDelete = { onDelete(group) },
                            themeViewModel = themeViewModel
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onDone,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .height(48.dp)
                .width(160.dp),
            shape = RoundedCornerShape(24.dp),
            containerColor = themeViewModel.addButtonColor,
            contentColor = themeViewModel.onPrimaryColor
        ) {
            Text("Færdig")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeIngredientRow(
    name: String,
    quantity: Int,
    unit: String,
    onDelete: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.4f },
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(themeViewModel.deleteColor)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    ) {
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeViewModel.surfaceColor)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = name,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = themeViewModel.textPrimary
                )

                Text(
                    text = "$quantity $unit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = themeViewModel.textSecondary
                )
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = themeViewModel.textSecondary.copy(alpha = 0.12f),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

