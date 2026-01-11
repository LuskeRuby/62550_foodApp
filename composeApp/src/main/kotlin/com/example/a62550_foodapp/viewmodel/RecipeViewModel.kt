package com.example.a62550_foodapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.RecipeDao
import com.example.a62550_foodapp.db.dao.RecipeItemDao
import com.example.a62550_foodapp.db.entity.Recipe as RecipeEntity
import com.example.a62550_foodapp.model.Recipe as RecipeModel
import com.example.a62550_foodapp.utils.saveRecipeImage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import com.example.a62550_foodapp.db.dao.ItemWeeklyPriceDao

class RecipeViewModel(
    private val recipeDao: RecipeDao,
    private val recipeItemDao: RecipeItemDao,
    private val itemWeeklyPriceDao: ItemWeeklyPriceDao,
    private val appContext: Context
) : ViewModel() {

    val recipes: StateFlow<List<RecipeModel>> = recipeDao.getAllRecipes()
        .map { entities ->
            entities.map { entity ->
                // read image bytes if available (previously assigned but unused)
                entity.imagePath?.let { path ->
                    try {
                        File(path).readBytes()
                    } catch (e: Exception) {
                        null
                    }
                }
                RecipeModel(
                    id = entity.id,
                    title = entity.title,
                    preparationTimeMinutes = entity.preparationTimeMinutes,
                    description = entity.description,
                    instructions = entity.instructions,
                    imagePath = entity.imagePath,
                    deletable = entity.deletable ?: true
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getRecipeById(id: Int): Flow<RecipeModel?> {
        return recipeDao.getRecipeById(id).map { entity ->
            entity?.let {
                it.imagePath?.let { path ->
                    try {
                        File(path).readBytes()
                    } catch (e: Exception) {
                        null
                    }
                }
                RecipeModel(
                    id = it.id,
                    title = it.title,
                    preparationTimeMinutes = it.preparationTimeMinutes,
                    description = it.description,
                    instructions = it.instructions,
                    imagePath = it.imagePath,
                    deletable = it.deletable ?: true
                )
            }
        }
    }

    fun createRecipe(
        title: String,
        description: String?,
        instructions: String?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            val recipeId = recipeDao.insert(
                RecipeEntity(
                    title = title,
                    preparationTimeMinutes = 30, //midlertidig default
                    description = description,
                    instructions = instructions,
                    imagePath = null,
                    deletable = true
                )
            ).toInt()

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

    // New: update an existing recipe's fields and optionally its image
    fun updateRecipe(
        id: Int,
        title: String,
        preparationTimeMinutes: Int,
        description: String?,
        instructions: String?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            // Update textual fields first
            recipeDao.updateRecipe(id, title, preparationTimeMinutes, description, instructions)

            // If a new image was provided, save and update imagePath
            if (imageUri != null) {
                val path = saveRecipeImage(
                    context = appContext,
                    sourceUri = imageUri,
                    recipeId = id
                )
                recipeDao.updateImagePath(id, path)
            }
        }
    }
}
