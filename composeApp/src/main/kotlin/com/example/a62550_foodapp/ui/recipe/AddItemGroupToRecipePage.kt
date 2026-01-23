package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.ui.components.SearchSelectField
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AddItemGroupToRecipePage(
    recipeViewModel: RecipeViewModel,
    onDone: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val allGroups by recipeViewModel.getAllItemGroups().collectAsState(initial = emptyList())
    val tempSelected by recipeViewModel.tempGroups.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<ItemGroup?>(null) }
    var qtyText by remember { mutableStateOf("") }
    val qtyFocusRequester = remember { FocusRequester() }

    LaunchedEffect(selectedGroup) {
        if (selectedGroup != null) {
            qtyFocusRequester.requestFocus()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeViewModel.backgroundColor) // GRÅ BAGGRUND
        ) {

            // ===== WHITE HEADER BLOCK =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeViewModel.surfaceColor) // HVID
            ) {
                Column {

                    // ---------- HEADER ----------
                    Text(
                        "Tilføj ingredienser",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 12.dp, bottom = 12.dp)
                    )

                    // ---------- INPUT ROW ----------
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp)
                    ) {

                        val rowHeight = TextFieldDefaults.MinHeight

                        // ---- SEARCH ----
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

                        // ---- QTY ----
                        OutlinedTextField(
                            value = qtyText,
                            onValueChange = { qtyText = it.filter(Char::isDigit) },
                            modifier = Modifier
                                .width(110.dp)
                                .height(rowHeight)
                                .focusRequester(qtyFocusRequester),
                            singleLine = true,
                            placeholder = { Text("0") },
                            trailingIcon = {
                                Text(
                                    text = selectedGroup?.unitType ?: "kg/L",
                                    color = themeViewModel.textSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeViewModel.addButtonColor,
                                unfocusedBorderColor = themeViewModel.addButtonColor,
                                cursorColor = themeViewModel.addButtonColor
                            )
                        )

                        // ---- ADD ----
                        Button(
                            onClick = {
                                val qty = qtyText.toIntOrNull() ?: return@Button
                                val group = selectedGroup ?: return@Button

                                recipeViewModel.addTempGroup(group.id, qty)

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

            // ===== GRÅ SPACER =====
            Spacer(modifier = Modifier.height(12.dp))

            // ---------- LIST ----------
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = themeViewModel.surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 0.dp)
                ) {
                    items(
                        tempSelected,
                        key = { it.itemGroupId }
                    ) { sg ->

                        val group = allGroups.firstOrNull { it.id == sg.itemGroupId }

                        EditRecipeItemRow(
                            name = group?.name ?: "(ukendt)",
                            quantity = sg.quantity,
                            unit = group?.unitType ?: "",
                            onDelete = { recipeViewModel.removeTempGroup(sg.itemGroupId) },
                            themeViewModel = themeViewModel
                        )
                    }
                }
            }
        }

        // ---------- DONE BUTTON ----------
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
            Text(text = "Færdig")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditRecipeItemRow(
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

                // ---- NAME ----
                Text(
                    text = name,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = themeViewModel.textPrimary
                )

                // ---- QUANTITY (ORANGE) ----
                Text(
                    text = "$quantity $unit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = themeViewModel.textSecondary
                )
            }

            Divider(
                thickness = 0.5.dp,
                color = themeViewModel.textSecondary.copy(alpha = 0.12f),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
