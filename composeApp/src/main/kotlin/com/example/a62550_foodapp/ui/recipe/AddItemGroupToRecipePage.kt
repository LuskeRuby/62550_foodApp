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


        Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
            .padding(16.dp)
    ) {

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
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
                }

            ) {
                Text("Tilføj")
            }
        }

        Spacer(Modifier.height(16.dp))

        // -------- SELECTED LIST --------
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
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
        }

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
