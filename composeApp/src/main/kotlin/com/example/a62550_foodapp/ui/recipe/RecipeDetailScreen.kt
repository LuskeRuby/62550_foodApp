package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.model.RecipeIngredient

@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    recipeViewModel: RecipeViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    val recipe by recipeViewModel.getRecipeById(recipeId).collectAsState(initial = null)

    var portions by remember { mutableStateOf(1) }

    val lightPink = Color(0xFFF3E5F5)
    val lightOrange = Color(0xFFFFCC80)

    recipe?.let { r ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {

            // ---- TITLE ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(lightOrange)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = r.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ---- LEFT: IMAGE ----
                Column(modifier = Modifier.weight(1f)) {

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFEEEEEE),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            "TILB. TID: ${r.preparationTimeMinutes} min",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                    ) {
                        AsyncImage(
                            model = r.imagePath,
                            contentDescription = r.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // ---- RIGHT: INGREDIENTS ----
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(lightPink)
                        .padding(12.dp)
                ) {
                    Text(
                        "Ingredienser",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // ---- INSTRUCTIONS ----
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = lightPink,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = r.instructions ?: "Ingen instruktioner tilgængelige.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
            }

            // ---- BOTTOM CONTROLS ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF5F5F5),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { if (portions > 1) portions-- }) {
                            Icon(Icons.Default.RemoveCircle, contentDescription = "Sænk portioner")
                        }

                        Text("$portions Portioner", fontWeight = FontWeight.Bold)

                        IconButton(onClick = { portions++ }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Øg portioner")
                        }
                    }
                }

                Button(
                    onClick = { /* TODO: Add to shopping list */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90EE90)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Tilføj til indkøbslisten", color = Color.Black)
                }

                // Edit button
                Button(onClick = { onEdit(r.id) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit")
                }
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
