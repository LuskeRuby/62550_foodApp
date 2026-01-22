package com.example.a62550_foodapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

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

    val focusRequester = remember { FocusRequester() }

    val matches =
        if (value.isNotBlank())
            items.filter { itemText(it).contains(value, ignoreCase = true) }
        else emptyList()

    Column {

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
                .focusRequester(focusRequester),
            singleLine = true,
            colors = colors
        )

        // sørg for at fokus altid bliver på feltet mens man søger
        LaunchedEffect(value, expanded) {
            if (expanded) focusRequester.requestFocus()
        }

        DropdownMenu(
            expanded = expanded && matches.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp),
            properties = PopupProperties(focusable = false)
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
