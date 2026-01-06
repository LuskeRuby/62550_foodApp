package com.example.a62550_foodapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.RecipeDao
import com.example.a62550_foodapp.db.entity.Recipe
import com.example.a62550_foodapp.utils.saveRecipeImage
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val recipeDao: RecipeDao,
    private val appContext: Context
) : ViewModel() {

    fun createRecipe(
        title: String,
        description: String?,
        instructions: String?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            // 1. Insert recipe without image
            val recipeId = recipeDao.insert(
                Recipe(
                    title = title,
                    description = description,
                    instructions = instructions,
                    imagePath = null,
                    deletable = true
                )
            ).toInt()

            // 2. Save image and update DB
            if (imageUri != null) {
                val path = saveRecipeImage(
                    context = appContext,
                    sourceUri = imageUri,
                    recipeId = recipeId
                )
                recipeDao.updateImagePath(recipeId, path)
            }
        }
    }
}
