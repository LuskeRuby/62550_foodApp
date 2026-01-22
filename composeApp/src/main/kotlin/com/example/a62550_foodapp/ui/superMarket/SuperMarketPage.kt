package com.example.a62550_foodapp.ui.superMarket

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.a62550_foodapp.db.entity.Supermarket
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SuperMarketPage(
    recipeViewModel: RecipeViewModel = koinViewModel(),
    storeFilterViewModel: StoreFilterViewModel = koinViewModel(),
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val allSupermarkets by recipeViewModel.allSupermarkets.collectAsStateWithLifecycle(emptyList())
    val selectedSupermarkets by storeFilterViewModel.selectedStores.collectAsStateWithLifecycle()

    // Select all supermarkets when first loaded (only if selection is empty)
    LaunchedEffect(allSupermarkets) {
        if (allSupermarkets.isNotEmpty() && selectedSupermarkets.isEmpty()) {
            storeFilterViewModel.selectAll(allSupermarkets.map { it.id }.toSet())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeViewModel.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // HEADER CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = themeViewModel.surfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Vælg supermarkeder",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeViewModel.textPrimary
                        )
                        Text(
                            text = "${selectedSupermarkets.size} af ${allSupermarkets.size} valgt",
                            fontSize = 14.sp,
                            color = themeViewModel.textSecondary
                        )
                    }

                    Button(
                        onClick = { storeFilterViewModel.clear() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeViewModel.cancelButton,
                            contentColor = themeViewModel.textPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = selectedSupermarkets.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Ryd",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ryd")
                    }
                }
            }

            if (allSupermarkets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ingen supermarkeder fundet",
                            fontSize = 16.sp,
                            color = themeViewModel.textSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allSupermarkets) { supermarket ->
                        SupermarketCard(
                            supermarket = supermarket,
                            isSelected = supermarket.id in selectedSupermarkets,
                            onClick = { storeFilterViewModel.toggleStore(supermarket.id) },
                            themeViewModel = themeViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SupermarketCard(
    supermarket: Supermarket,
    isSelected: Boolean,
    onClick: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val borderColor = if (isSelected) themeViewModel.primaryColor else Color.LightGray
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = themeViewModel.surfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo or Name
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (supermarket.logo != null) {
                    AsyncImage(
                        model = supermarket.logo,
                        contentDescription = supermarket.name,
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = 180.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = supermarket.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeViewModel.textPrimary
                    )
                }
            }

            // Checkbox
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = themeViewModel.primaryColor,
                    uncheckedColor = themeViewModel.textSecondary,
                    checkmarkColor = themeViewModel.onPrimaryColor
                )
            )
        }
    }
}
