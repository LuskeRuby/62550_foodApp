package com.example.a62550_foodapp.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlaylistAddCheck
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
                modifier = Modifier.height(80.dp),
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
                            modifier = Modifier.padding(top = 12.dp)
                        ) 
                    },
                    text = { 
                        Text(
                            "Recipes",
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) 
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { 
                        Icon(
                            Icons.Default.List, 
                            contentDescription = "Shopping List",
                            modifier = Modifier.padding(top = 12.dp)
                        ) 
                    },
                    text = { 
                        Text(
                            "Shopping List",
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) 
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { 
                        Icon(
                            Icons.Default.Fastfood, 
                            contentDescription = "Food",
                            modifier = Modifier.padding(top = 12.dp)
                        ) 
                    },
                    text = { 
                        Text(
                            "Food",
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) 
                    }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { 
                        Icon(
                            Icons.Default.PlaylistAddCheck, 
                            contentDescription = "Details",
                            modifier = Modifier.padding(top = 12.dp)
                        ) 
                    },
                    text = { 
                        Text(
                            "Details",
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) 
                    }
                )
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> RecipePage()
                1 -> ShoppingListPage()
                2 -> FoodItemPage()
                3 -> ShoppingListDetailsPage()
            }
        }
    }
}
