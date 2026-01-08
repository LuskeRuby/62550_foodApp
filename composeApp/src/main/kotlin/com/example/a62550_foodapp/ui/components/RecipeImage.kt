package com.example.a62550_foodapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.io.File
import androidx.annotation.DrawableRes
import com.example.a62550_foodapp.R

@Composable
fun LocalImage(
    imagePath: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    @DrawableRes fallbackRes: Int = R.drawable.picture_missing
) {
    val file = imagePath
        ?.takeIf { it.isNotBlank() }
        ?.let { File(it) }
        ?.takeIf { it.exists() }

    if (file == null) {
        AsyncImage(
            model = fallbackRes,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
        return
    }

    AsyncImage(
        model = File(imagePath),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
