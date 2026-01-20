package com.example.a62550_foodapp.ui.superMarket

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SuperMarketPage(
    recipeViewModel: RecipeViewModel = koinViewModel(),
    storeFilterViewModel: StoreFilterViewModel = koinViewModel()
) {

    val allSupermarkets by recipeViewModel.allSupermarkets.collectAsState(initial = emptyList())
    val selectedSupermarkets by storeFilterViewModel.selectedStores.collectAsState()

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
                            .clickable {
                                storeFilterViewModel.toggleStore(supermarket.id)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = supermarket.id in selectedSupermarkets,
                            onCheckedChange = {
                                storeFilterViewModel.toggleStore(supermarket.id)
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = supermarket.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
