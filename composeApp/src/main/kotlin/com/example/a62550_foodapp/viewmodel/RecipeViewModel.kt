package com.example.a62550_foodapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.RecipeDao
import com.example.a62550_foodapp.db.dao.RecipeItemDao
import com.example.a62550_foodapp.db.entity.Recipe as RecipeEntity
import com.example.a62550_foodapp.model.Recipe as RecipeModel
import com.example.a62550_foodapp.model.RecipeIngredient
import com.example.a62550_foodapp.utils.saveRecipeImage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class RecipeViewModel(
    private val recipeDao: RecipeDao,
    private val recipeItemDao: RecipeItemDao,
    private val appContext: Context
) : ViewModel() {

    val recipes: StateFlow<List<RecipeModel>> = recipeDao.getAllRecipes()
        .map { entities ->
            entities.map { entity ->
                val byteArray = entity.imagePath?.let { path ->
                    try {
                        File(path).readBytes()
                    } catch (e: Exception) {
                        null
                    }
                }
                RecipeModel(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    instructions = entity.instructions,
                    picture = byteArray,
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
                val byteArray = it.imagePath?.let { path ->
                    try {
                        File(path).readBytes()
                    } catch (e: Exception) {
                        null
                    }
                }
                RecipeModel(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    instructions = it.instructions,
                    picture = byteArray,
                    deletable = it.deletable ?: true
                )
            }
        }
    }

    fun getRecipePrice(id: Int): Flow<Float> {
        return recipeDao.getRecipeTotalPrice(id).map { it ?: 0f }
    }

    fun getIngredients(recipeId: Int): Flow<List<RecipeIngredient>> {
        return recipeItemDao.getIngredientsForRecipe(recipeId)
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
}
