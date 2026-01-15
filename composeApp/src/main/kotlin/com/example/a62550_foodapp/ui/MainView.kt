package com.example.a62550_foodapp.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.ui.recipe.RecipePage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    recipeContent: @Composable () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val creamyOrange = Color(0xFFFFD59A)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp,
                windowInsets = NavigationBarDefaults.windowInsets //so it does not go behind the android bar
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = "Recipes",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            "Opskrifter",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 0)
                                FontWeight.SemiBold
                            else
                                FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.Black,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "Shopping List",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            "Indkøbsliste",
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == 1)
                                FontWeight.SemiBold
                            else
                                FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.Black,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
            color = Color(0xFFF5F5F5) // light grey app background
        ) {
            when (selectedTab) {
                0 -> recipeContent()
                1 -> ShoppingListPage()
            }
        }
    }
}