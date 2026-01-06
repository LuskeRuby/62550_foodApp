package com.example.a62550_foodapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.di.getViewModel
import com.example.a62550_foodapp.viewmodel.MainViewModel

@Composable
fun FoodItemPage() {
    val viewModel: MainViewModel = getViewModel()
    val foodItems by viewModel.foodItems.collectAsState()

    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Calories") },
                modifier = Modifier.weight(1f)
            )
        }
        Button(onClick = {
            val cal = calories.toIntOrNull() ?: 0
            viewModel.addFoodItem(name, cal)
            name = ""
            calories = ""
        }) {
            Text("Add Food Item")
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(foodItems) { item ->
                Text("Name: ${item.name}, Calories: ${item.calories}", modifier = Modifier.padding(8.dp))
            }
        }
    }
}
