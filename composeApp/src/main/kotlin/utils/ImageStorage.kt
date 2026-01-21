package com.example.a62550_foodapp.utils

import android.content.Context
import android.net.Uri
import java.io.File
import androidx.annotation.DrawableRes

fun saveRecipeImage(
    context: Context,
    sourceUri: Uri,
    recipeId: Long
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

fun copyDrawableToInternalStorage(
    context: Context,
    @DrawableRes drawableRes: Int,
    targetFileName: String
): String {
    val targetFile = File(context.filesDir, targetFileName)

    context.resources.openRawResource(drawableRes).use { input ->
        targetFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return targetFile.absolutePath
}