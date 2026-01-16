package com.example.a62550_foodapp.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import coil.compose.AsyncImage
import com.example.a62550_foodapp.model.Ingredient
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    recipeViewModel: RecipeViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val recipe by recipeViewModel.getRecipeById(recipeId).collectAsState(initial = null)
    val recipeItems by recipeViewModel
        .getItemsForRecipeFlow(recipeId)
        .collectAsState(initial = emptyList())

    var portions by remember { mutableStateOf(1) }
    var scaledPrice by remember { mutableStateOf(0f) }
    var resolvedIngredients by remember { mutableStateOf<List<Ingredient>>(emptyList()) }

    LaunchedEffect(portions) {
        scaledPrice = recipeViewModel.getRecipePriceByPortions(recipeId, portions)
    }

    LaunchedEffect(recipeItems, portions) {
        resolvedIngredients = recipeItems.map { ri ->
            recipeViewModel.resolveIngredient(
                itemGroupId = ri.itemGroupId,
                quantity = ri.quantity * portions
            )
        }
    }

    val lightPink = Color(0xFFF3E5F5)

    recipe?.let { r ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeViewModel.backgroundColor)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 96.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    AsyncImage(
                        model = r.imagePath,
                        contentDescription = r.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x33000000))
                    )

                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = themeViewModel.secondaryColor,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            "⏱ ${r.preparationTimeMinutes} min",
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                            color = themeViewModel.onSecondaryColor
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFD32F2F),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            String.format(Locale.getDefault(), "%.2f kr", scaledPrice),
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { onEdit(r.id) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, null, tint = Color.White)
                    }
                }

                Text(
                    text = r.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = themeViewModel.textPrimary,
                    modifier = Modifier.padding(16.dp)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = lightPink,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Ingredienser", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))

                        if (resolvedIngredients.isEmpty()) {
                            Text("Ingen ingredienser angivet.")
                        } else {
                            resolvedIngredients.forEach { ing ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(ing.groupName)
                                    Text(
                                        buildString {
                                            val q = ing.quantity
                                            append(
                                                if (q % 1f == 0f) q.toInt() else String.format(
                                                    Locale.getDefault(),
                                                    "%.1f",
                                                    q
                                                )
                                            )
                                            append(" ")
                                            append(ing.unitType)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

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
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Surface(
                tonalElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (portions > 1) portions-- }) {
                                Icon(Icons.Default.RemoveCircle, null, tint = Color.Red)
                            }
                            Text(portions.toString(), fontWeight = FontWeight.Bold)
                            IconButton(onClick = { portions++ }) {
                                Icon(Icons.Default.AddCircle, null, tint = Color.Red)
                            }
                        }
                        Text("Personer", color = Color.Red)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { }
                    ) {
                        Icon(Icons.Default.AddCircle, null, tint = Color.Red)
                        Text("Tilføj til liste", color = Color.Red)
                    }
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
