package com.example.a62550_foodapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchSelectField(
    label: String,
    items: List<T>,
    itemText: (T) -> String,
    itemUnit: (T) -> String? = { null },

    value: String,
    onValueChange: (String) -> Unit,

    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val matches =
        if (value.isNotBlank())
            items.filter { itemText(it).contains(value, ignoreCase = true) }
        else emptyList()

    ExposedDropdownMenuBox(
        expanded = expanded && matches.isNotEmpty(),
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            placeholder = { Text(label) }, // valgfrit: kun som hint i feltet
            modifier = Modifier
                .fillMaxWidth()
                .height(TextFieldDefaults.MinHeight)
                .menuAnchor(),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded && matches.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 240.dp)
        ) {
            matches.take(5).forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemText(item)) },
                    onClick = {
                        onValueChange(itemText(item))
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
