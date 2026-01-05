package com.example.a62550_foodapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
    var selectedTab by remember { mutableStateOf(0) }
    val creamyOrange = Color(0xFFFFD59A)
    
    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = creamyOrange,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color.Black
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { 
                        Icon(
                            Icons.Default.Restaurant, 
                            contentDescription = "Recipes",
                            modifier = Modifier.padding(top = 8.dp)
                        ) 
                    },
                    text = { Text("Recipes") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { 
                        Icon(
                            Icons.Default.List, 
                            contentDescription = "Shopping List",
                            modifier = Modifier.padding(top = 8.dp)
                        ) 
                    },
                    text = { Text("Shopping List") }
                )
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> RecipePage()
                1 -> ShoppingListPage()
            }
        }
    }
}
