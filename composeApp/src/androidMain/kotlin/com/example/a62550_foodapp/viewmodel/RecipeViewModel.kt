package com.example.a62550_foodapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.RecipeDao
import com.example.a62550_foodapp.db.entity.Recipe as RecipeEntity
import com.example.a62550_foodapp.model.Recipe as RecipeModel
import com.example.a62550_foodapp.utils.saveRecipeImage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val recipeDao: RecipeDao,
    private val appContext: Context
) : ViewModel() {

    val recipes: StateFlow<List<RecipeModel>> = recipeDao.getAllRecipes()
        .map { entities ->
            entities.map { entity ->
                RecipeModel(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    instructions = entity.instructions,
                    picture = null, // We'll handle image loading differently (via imagePath)
                    deletable = entity.deletable ?: true
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createRecipe(
        title: String,
        description: String?,
        instructions: String?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            // 1. Insert recipe without image
            val recipeId = recipeDao.insert(
                RecipeEntity(
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
