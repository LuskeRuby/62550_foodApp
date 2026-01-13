package com.example.a62550_foodapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.RecipeDao
import com.example.a62550_foodapp.db.dao.RecipeItemDao
import com.example.a62550_foodapp.db.entity.RecipeItem
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.db.dao.ItemWeeklyPriceDao
import com.example.a62550_foodapp.db.dao.ItemGroupDao
import com.example.a62550_foodapp.db.entity.Recipe as RecipeEntity
import com.example.a62550_foodapp.model.Recipe as RecipeModel
import com.example.a62550_foodapp.utils.saveRecipeImage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.io.File

// Represent a selection of an existing ItemGroup when creating a recipe
data class SelectedItemGroup(
    val itemGroupId: Int,
    val quantity: Float
)

class RecipeViewModel(
    private val recipeDao: RecipeDao,
    private val recipeItemDao: RecipeItemDao,
    private val itemWeeklyPriceDao: ItemWeeklyPriceDao,
    private val appContext: Context,
    private val itemGroupDao: ItemGroupDao
) : ViewModel() {

    val recipes: StateFlow<List<RecipeModel>> = recipeDao.getAllRecipes()
        .map { entities ->
            entities.map { entity ->
                // read image bytes if available (previously assigned but unused)
                entity.imagePath?.let { path ->
                    try {
                        File(path).readBytes()
                    } catch (_: Exception) {
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
                    } catch (_: Exception) {
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

    // Expose existing item groups for the UI to select from
    fun getAllItemGroups(): Flow<List<ItemGroup>> = itemGroupDao.getAllItemGroups()

    // Expose recipe items (associations of item group + quantity) as a Flow for the UI
    fun getItemsForRecipeFlow(recipeId: Int) = kotlinx.coroutines.flow.flow {
        emit(recipeItemDao.getItemsForRecipe(recipeId))
    }

    fun createRecipe(
        title: String,
        preparationTimeMinutes: Int,
        description: String?,
        instructions: String?,
        imageUri: Uri?,
        selectedGroups: List<SelectedItemGroup> = emptyList()
    ) {
        viewModelScope.launch {
            val recipeId = recipeDao.insert(
                RecipeEntity(
                    title = title,
                    preparationTimeMinutes = preparationTimeMinutes,
                    description = description,
                    instructions = instructions,
                    imagePath = null,
                    deletable = true
                )
            ).toInt()

            // Link selected existing item groups to the created recipe
            if (selectedGroups.isNotEmpty()) {
                selectedGroups.forEach { sg ->
                    try {
                        recipeItemDao.insert(RecipeItem(recipeId, sg.itemGroupId, sg.quantity))
                    } catch (_: Exception) {
                        // ignore failures for now (minimal change)
                    }
                }
            }

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

    // Update an existing recipe's fields and optionally its image
    fun updateRecipe(
        id: Int,
        title: String,
        preparationTimeMinutes: Int,
        description: String?,
        instructions: String?,
        imageUri: Uri?,
        selectedGroups: List<SelectedItemGroup>
    ) {
        viewModelScope.launch {

            // 1) Update recipe text fields
            recipeDao.updateRecipe(
                id,
                title,
                preparationTimeMinutes,
                description ?: "",
                instructions ?: ""
            )

            // 2) ALWAYS update recipe_items
            recipeItemDao.deleteForRecipe(id)

            val items = selectedGroups.map {
                RecipeItem(
                    recipeId = id,
                    itemGroupId = it.itemGroupId,
                    quantity = it.quantity
                )
            }

            recipeItemDao.insertAll(items)

            // 3) Update image ONLY if a new one was provided
            if (imageUri != null) {
                val existingPath = recipeDao.getRecipeById(id).first()?.imagePath
                existingPath?.let {
                    try {
                        val f = File(it)
                        if (f.exists()) f.delete()
                    } catch (_: Exception) { }
                }

                val path = saveRecipeImage(
                    context = appContext,
                    sourceUri = imageUri,
                    recipeId = id
                )
                recipeDao.updateImagePath(id, path)
            }
        }
    }

    // Remove image association for a recipe and delete the app-managed image file if present
    fun removeImage(recipeId: Int) {
        viewModelScope.launch {
            try {
                val existingPath = recipeDao.getRecipeById(recipeId).first()?.imagePath
                existingPath?.let { path ->
                    try {
                        val f = File(path)
                        val filesDir = appContext.filesDir
                        if (f.exists() && f.canonicalPath.startsWith(filesDir.canonicalPath)) {
                            f.delete()
                        }
                    } catch (_: Exception) {
                        // ignore deletion failures
                    }
                }
            } catch (_: Exception) {
                // ignore failures getting existing path
            }

            // Clear DB reference
            recipeDao.updateImagePath(recipeId, null)
        }
    }

    suspend fun getSelectedGroupsForRecipe(recipeId: Int): List<SelectedItemGroup> {
        return recipeItemDao.getItemsForRecipe(recipeId).map {
            SelectedItemGroup(
                itemGroupId = it.itemGroupId,
                quantity = it.quantity
            )
        }
    }

}
