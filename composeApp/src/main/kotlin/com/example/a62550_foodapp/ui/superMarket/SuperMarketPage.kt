package com.example.a62550_foodapp.ui.superMarket

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SuperMarketPage(
    recipeViewModel: RecipeViewModel = koinViewModel(),
    storeFilterViewModel: StoreFilterViewModel = koinViewModel()
) {

    val allSupermarkets by recipeViewModel.allSupermarkets.collectAsStateWithLifecycle(emptyList())
    val selectedSupermarkets by storeFilterViewModel.selectedStores.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // HEADER ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Vælg supermarkeder",
                style = MaterialTheme.typography.titleLarge
            )

            if (selectedSupermarkets.isNotEmpty()) {
                TextButton(
                    onClick = { storeFilterViewModel.clear() }
                ) {
                    Text("Clear")
                }
            }
        }

        if (allSupermarkets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Ingen supermarkeder fundet")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allSupermarkets.forEach { supermarket ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                            .clickable {
                                storeFilterViewModel.toggleStore(supermarket.id)
                            }
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (supermarket.logo != null) {
                            AsyncImage(
                                model = supermarket.logo,
                                contentDescription = supermarket.name,
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(70.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Text(
                                text = supermarket.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.width(150.dp)
                            )
                        }

                        Checkbox(
                            checked = supermarket.id in selectedSupermarkets,
                            onCheckedChange = null // Row håndterer klik
                        )
                    }
                }
            }
        }
    }
}
