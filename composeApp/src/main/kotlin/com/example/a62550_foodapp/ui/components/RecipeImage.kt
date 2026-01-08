package com.example.a62550_foodapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.io.File

@Composable
fun LocalImage(
    imagePath: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    if (imagePath.isNullOrBlank()) {
        Box(modifier = modifier)
        return
    }

    AsyncImage(
        model = File(imagePath),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
