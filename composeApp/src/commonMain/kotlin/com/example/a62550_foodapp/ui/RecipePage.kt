package com.example.a62550_foodapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.a62550_foodapp.model.Recipe

@Composable
fun RecipePage() {
    // Placeholder list - this will be replaced with database data later
    val recipes = remember {
        listOf(
            Recipe(1, "Pasta Carbonara", "Classic Italian pasta", null, null, false),
            Recipe(2, "Chicken Salad", "Fresh and healthy", null, null, false),
            Recipe(3, "Beef Stew", "Hearty winter meal", null, null, false),
            Recipe(4, "Pancakes", "Fluffy breakfast treats", null, null, false)
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(recipes) { recipe ->
            RecipeCard(recipe)
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Makes the items square blocks
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Placeholder for image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f) // Reserved 60% of the card for the picture
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Here you can later add the Image() composable
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f) // Remaining 40% for text
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                recipe.description?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
