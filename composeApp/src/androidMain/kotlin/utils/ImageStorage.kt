package com.example.a62550_foodapp.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun saveRecipeImage(
    context: Context,
    sourceUri: Uri,
    recipeId: Int
): String {
    val fileName = "recipe_$recipeId.jpg"
    val file = File(context.filesDir, fileName)

    context.contentResolver.openInputStream(sourceUri).use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
        }
    }

    return file.absolutePath
}
