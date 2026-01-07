package com.example.a62550_foodapp.ui.icons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PencilIcon: ImageVector = Builder(
    name = "Pencil",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(fill = SolidColor(Color.Black)) {
        moveTo(3f, 17f)
        lineTo(3f, 21f)
        lineTo(7f, 21f)
        lineTo(18f, 10f)
        lineTo(14f, 6f)
        lineTo(3f, 17f)
        close()

        moveTo(19.7f, 7.0f)
        lineTo(16.0f, 3.3f)
        lineTo(20.7f, -1.4f)
    }
}.build()