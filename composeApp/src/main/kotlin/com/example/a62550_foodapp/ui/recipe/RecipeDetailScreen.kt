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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalDensity

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
    var scaledPrice by remember { mutableStateOf(0f) }

    LaunchedEffect(portions) {
        scaledPrice = recipeViewModel.getRecipePriceByPortions(recipeId, portions)
    }

    val scrollState = rememberScrollState()

    val maxImageHeight = 260.dp
    val minImageHeight = 96.dp

    val collapseFraction =
        (scrollState.value / 300f).coerceIn(0f, 1f)

    val imageHeight =
        maxImageHeight - (maxImageHeight - minImageHeight) * collapseFraction

    recipe?.let { r ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(themeViewModel.backgroundColor)
        ) {

            // ================= HEADER =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
            ) {
                AsyncImage(
                    model = r.imagePath,
                    contentDescription = r.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // EDIT
                IconButton(
                    onClick = { onEdit(r.id) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.White)
                }

                // CLOSE
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Icon(Icons.Default.Close, null, tint = Color.White)
                }

                // ===== MOVING TITLE =====
                val titleY =
                    (imageHeight - 48.dp) - (imageHeight - minImageHeight) * collapseFraction

                Text(
                    text = r.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 64.dp)
                        .offset(y = titleY)
                        .align(Alignment.TopCenter)
                )
            }

            // ================= ACTION BAR (STICKY) =================
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 👥 PERSONER
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (portions > 1) portions-- }) {
                                Icon(Icons.Default.RemoveCircle, null, tint = themeViewModel.priceTagColor)
                            }
                            Text(portions.toString(), fontWeight = FontWeight.Bold)
                            IconButton(onClick = { portions++ }) {
                                Icon(Icons.Default.AddCircle, null, tint = themeViewModel.priceTagColor)
                            }
                        }
                        Text("Personer", color = themeViewModel.priceTagColor)
                    }

                    // ⏱ TID
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⏱ ${r.preparationTimeMinutes} min", fontWeight = FontWeight.Bold)
                        Text("Tid", color = themeViewModel.priceTagColor)
                    }

                    // 💰 PRIS
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(String.format(Locale.getDefault(), "%.2f kr", scaledPrice), fontWeight = FontWeight.Bold)
                        Text("Pris", color = themeViewModel.priceTagColor)
                    }

                    // ➕ TILFØJ
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { }
                    ) {
                        Icon(Icons.Default.AddCircle, null, tint = themeViewModel.priceTagColor)
                        Text("Tilføj", color = themeViewModel.priceTagColor)
                    }
                }
            }

            // ================= SCROLLING CONTENT =================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp)
            ) {

                Spacer(Modifier.height(16.dp))

                // INGREDIENTS
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = Color(0xFFF3E5F5),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Ingredienser", fontWeight = FontWeight.Bold)

                        val allGroups by recipeViewModel.getAllItemGroups()
                            .collectAsState(initial = emptyList())

                        val recipeItems by recipeViewModel
                            .getItemsForRecipeFlow(r.id)
                            .collectAsState(initial = emptyList())

                        Spacer(Modifier.height(8.dp))

                        recipeItems.forEach { ri ->
                            val group = allGroups.firstOrNull { it.id == ri.itemGroupId }
                            val unit = group?.unitType ?: ""
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(group?.name ?: "")
                                Text("${ri.quantity * portions} $unit")
                            }
                        }
                    }
                }

                // INSTRUCTIONS
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color(0xFFF3E5F5),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        r.instructions ?: "Ingen instruktioner",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
