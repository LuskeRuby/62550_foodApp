package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
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
import java.util.Locale
import coil.compose.AsyncImage
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    recipeViewModel: RecipeViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val recipe by recipeViewModel.getRecipeById(recipeId).collectAsState(initial = null)

    var portions by remember { mutableStateOf(1) }

    val lightPink = Color(0xFFF3E5F5)

    recipe?.let { r ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(themeViewModel.backgroundColor)
        ) {

            // ---- HEADER WITH BACK BUTTON AND TITLE (Matching Nav Bar) ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeViewModel.navBarColor)
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = themeViewModel.onNavBarColor
                    )
                }

                Text(
                    text = r.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = themeViewModel.onNavBarColor,
                    modifier = Modifier.align(Alignment.Center)
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
                        color = themeViewModel.secondaryColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            "TILB. TID: ${r.preparationTimeMinutes} min",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = themeViewModel.onSecondaryColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(themeViewModel.secondaryColor)
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
                    // Collect item groups and recipe items to display ingredient list
                    val allGroups by recipeViewModel.getAllItemGroups().collectAsState(initial = emptyList())
                    val recipeItems by recipeViewModel.getItemsForRecipeFlow(r.id).collectAsState(initial = emptyList())

                    if (recipeItems.isEmpty()) {
                        Text("Ingen ingredienser angivet.", modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Column {
                            recipeItems.forEach { ri ->
                                val group = allGroups.firstOrNull { it.id == ri.itemGroupId }
                                val groupName = group?.name ?: "(ukendt)"
                                val unit = group?.unitType ?: ""
                                val scaledQty = ri.quantity * portions
                                fun formatQty(q: Float): String {
                                    return if (q % 1f == 0f) q.toInt().toString() else String.format(Locale.getDefault(), "%.1f", q)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(groupName, style = MaterialTheme.typography.bodyLarge)
                                        if (unit.isNotBlank()) {
                                            Text(unit, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    Text("${formatQty(scaledQty)} ${unit}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
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
                    color = themeViewModel.secondaryColor,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { if (portions > 1) portions-- }) {
                            Icon(Icons.Default.RemoveCircle, contentDescription = "Sænk portioner", tint = themeViewModel.primaryColor)
                        }

                        Text("$portions Portioner", fontWeight = FontWeight.Bold, color = themeViewModel.onSecondaryColor)

                        IconButton(onClick = { portions++ }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Øg portioner", tint = themeViewModel.primaryColor)
                        }
                    }
                }

                Button(
                    onClick = { /* TODO: Add to shopping list */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = themeViewModel.primaryColor),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Tilføj til indkøbslisten", color = themeViewModel.onPrimaryColor)
                }

                // Edit button
                IconButton(onClick = { onEdit(r.id) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = themeViewModel.textSecondary)
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
