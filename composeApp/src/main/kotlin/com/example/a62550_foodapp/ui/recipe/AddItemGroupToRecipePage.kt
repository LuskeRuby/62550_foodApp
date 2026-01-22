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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.ui.components.SearchSelectField
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun AddItemGroupToRecipePage(
    recipeViewModel: RecipeViewModel,
    onDone: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val allGroups by recipeViewModel.getAllItemGroups().collectAsState(initial = emptyList())
    val tempSelected by recipeViewModel.tempGroups.collectAsState()

    var selectedGroup by remember { mutableStateOf<ItemGroup?>(null) }
    var searchText by remember { mutableStateOf("") }
    var qtyText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
            .padding(16.dp)
    ) {

        item {
            Text(
                "Tilføj ingredienser",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            // -------- SEARCH --------
            SearchSelectField(
                label = "Søg ingrediens",
                items = allGroups,
                itemText = { it.name },
                itemUnit = { it.unitType },
                value = searchText,
                onValueChange = { searchText = it },
                onItemSelected = { entry ->
                    selectedGroup = entry
                }
            )

            Spacer(Modifier.height(8.dp))

            // -------- QTY + UNIT + ADD --------
            val rowHeight = TextFieldDefaults.MinHeight

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { qtyText = it.filter(Char::isDigit) },
                    modifier = Modifier
                        .width(92.dp)
                        .defaultMinSize(minHeight = rowHeight),
                    singleLine = true
                )

                Box(
                    modifier = Modifier
                        .height(rowHeight)
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = selectedGroup?.unitType ?: "",
                        fontSize = 14.sp,
                        color = themeViewModel.textSecondary
                    )
                }

                Button(
                    enabled = selectedGroup != null && qtyText.isNotBlank(),
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
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text("Tilføj")
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // -------- SELECTED LIST --------
        items(tempSelected, key = { it.itemGroupId }) { sg ->

            val name =
                allGroups.firstOrNull { it.id == sg.itemGroupId }?.name ?: "(ukendt)"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "$name — ${sg.quantity}",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium
                )

                TextButton(
                    onClick = { recipeViewModel.removeTempGroup(sg.itemGroupId) }
                ) {
                    Text("Fjern", color = themeViewModel.errorColor)
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))

            // -------- DONE --------
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Færdig")
            }
        }
    }
}
