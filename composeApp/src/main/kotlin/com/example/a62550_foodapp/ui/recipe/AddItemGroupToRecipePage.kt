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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
    ) {

        // ---------- HEADER ----------
        Text(
            "Tilføj ingredienser",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // ---------- INPUT ROW ----------
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
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
            val canAdd = selectedGroup != null && qtyText.isNotBlank()

            OutlinedTextField(
                value = qtyText,
                onValueChange = { qtyText = it.filter(Char::isDigit) },
                modifier = Modifier
                    .width(88.dp)
                    .height(rowHeight),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeViewModel.addButtonColor,
                    unfocusedBorderColor = themeViewModel.addButtonColor,
                    cursorColor = themeViewModel.addButtonColor
                )
            )

            // ---- UNIT ----
            Box(
                modifier = Modifier.height(rowHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedGroup?.unitType ?: "",
                    fontSize = 14.sp,
                    color = themeViewModel.textSecondary
                )
            }

            // ---- ADD ----
            Button(
                enabled = canAdd,
                onClick = {
                    val qty = qtyText.toIntOrNull() ?: return@Button

                    selectedGroup?.let {
                        recipeViewModel.addTempGroup(it.id, qty)
                    }

                    qtyText = ""
                    selectedGroup = null
                    searchText = ""
                },
                modifier = Modifier.height(rowHeight),
                contentPadding = PaddingValues(horizontal = 20.dp),
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

        // ---------- LIST ----------
        LazyColumn(
            modifier = Modifier.padding(top = 12.dp)
        ) {
            items(
                tempSelected,
                key = { it.itemGroupId }
            ) { sg ->

                val group = allGroups.firstOrNull { it.id == sg.itemGroupId }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
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
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = onDone,
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
                    .background(Color.Red)
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(themeViewModel.backgroundColor) // eller card color
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    color = themeViewModel.textPrimary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "$quantity $unit",
                    fontSize = 13.sp,
                    color = themeViewModel.textPrimary,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
