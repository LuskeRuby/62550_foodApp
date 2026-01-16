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

@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    recipeViewModel: RecipeViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val recipe by recipeViewModel.getRecipeById(recipeId).collectAsState(initial = null)
    val basePrice by recipeViewModel.getRecipePriceFlow(recipeId).collectAsState(initial = 0f)
    var portions by remember { mutableStateOf(1) }
    var scaledPrice by remember { mutableStateOf(0f) }

    // Update scaled price whenever portions change
    LaunchedEffect(portions) {
        scaledPrice = recipeViewModel.getRecipePriceByPortions(recipeId, portions)
    }

    val lightPink = Color(0xFFF3E5F5)
    val scrollState = rememberScrollState()

    //for scrollable image
    val maxImageHeight = 260.dp
    val minImageHeight = 96.dp
    val collapseRange = 300f

    val collapseFraction =
        (scrollState.value / collapseRange).coerceIn(0f, 1f)

    val imageHeight =
        maxImageHeight - (maxImageHeight - minImageHeight) * collapseFraction

    val titleOffsetY = (-40 * collapseFraction).dp
    val titleAlpha = collapseFraction


    recipe?.let { r ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeViewModel.backgroundColor)
        ) {

            // ===== COLLAPSING IMAGE HEADER (STICKY) =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .align(Alignment.TopCenter)
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
                        .align(Alignment.TopCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.6f * collapseFraction),
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
            }

            // ===== MOVING TITLE =====
            val titleY = imageHeight - scrollState.value.dp

            Text(
                text = r.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (scrollState.value > 120) Color.White else themeViewModel.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = titleY.coerceAtLeast(minImageHeight))
            )




            // ===== SCROLLING CONTENT =====
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(
                        top = imageHeight + 56.dp,
                        bottom = 96.dp
                    )
            ) {

                // ===== INGREDIENTS =====
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = lightPink,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            "Ingredienser",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val allGroups by recipeViewModel.getAllItemGroups()
                            .collectAsState(initial = emptyList())

                        val recipeItems by recipeViewModel
                            .getItemsForRecipeFlow(r.id)
                            .collectAsState(initial = emptyList())

                        if (recipeItems.isEmpty()) {
                            Text("Ingen ingredienser angivet.")
                        } else {
                            recipeItems.forEach { ri ->
                                val group = allGroups.firstOrNull { it.id == ri.itemGroupId }
                                val groupName = group?.name ?: "(ukendt)"
                                val unit = group?.unitType ?: ""
                                val scaledQty = ri.quantity * portions

                                fun formatQty(q: Float): String =
                                    if (q % 1f == 0f) q.toInt().toString()
                                    else String.format(Locale.getDefault(), "%.1f", q)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(groupName)
                                    Text("${formatQty(scaledQty)} $unit")
                                }
                            }
                        }
                    }
                }

                // ===== INSTRUCTIONS =====
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
            }

            // ================= BOTTOM ACTION BAR =================
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

                    // 👥 PERSONER
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (portions > 1) portions-- }) {
                                Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = themeViewModel.priceTagColor)
                            }
                            Text(
                                text = portions.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            IconButton(onClick = { portions++ }) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, tint = themeViewModel.priceTagColor)
                            }
                        }
                        Text("Personer", color = themeViewModel.priceTagColor)
                    }

                    // ⏱ TID
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⏱ ${r.preparationTimeMinutes} min",
                            fontWeight = FontWeight.Bold
                        )
                        Text("Tid", color = themeViewModel.priceTagColor)
                    }

                    // 💰 PRIS
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f kr", scaledPrice),
                            fontWeight = FontWeight.Bold
                        )
                        Text("Pris", color = themeViewModel.priceTagColor)
                    }

                    // ➕ TILFØJ TIL LISTE
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /* TODO */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = themeViewModel.priceTagColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("Tilføj", color = themeViewModel.priceTagColor)
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

