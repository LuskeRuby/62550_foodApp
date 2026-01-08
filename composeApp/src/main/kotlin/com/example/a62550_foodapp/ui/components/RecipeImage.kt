package com.example.a62550_foodapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File

@Composable
fun RecipeImage(
    imagePath: String?,
    modifier: Modifier = Modifier
) {
    if (imagePath.isNullOrBlank()) {
        Box(modifier = modifier)
        return
    }

    AsyncImage(
        model = File(imagePath),
        contentDescription = "Recipe image",
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

