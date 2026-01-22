package com.example.a62550_foodapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchSelectField(
    label: String,
    items: List<T>,
    itemText: (T) -> String,

    value: String,
    onValueChange: (String) -> Unit,
    onItemSelected: (T) -> Unit,

    borderColor: Color? = null
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

        val colors =
            if (borderColor != null)
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor,
                    cursorColor = borderColor
                )
            else
                OutlinedTextFieldDefaults.colors()

        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            placeholder = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .height(TextFieldDefaults.MinHeight)
                .menuAnchor(),
            singleLine = true,
            colors = colors
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