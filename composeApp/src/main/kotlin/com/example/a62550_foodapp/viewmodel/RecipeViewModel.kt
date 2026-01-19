package com.example.a62550_foodapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.*
import com.example.a62550_foodapp.db.entity.RecipeItem
import com.example.a62550_foodapp.db.entity.Recipe as RecipeEntity
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.db.entity.Supermarket
import com.example.a62550_foodapp.model.Recipe as RecipeModel
import com.example.a62550_foodapp.model.Ingredient
import com.example.a62550_foodapp.utils.saveRecipeImage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.io.File
import kotlin.math.ceil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update



data class SelectedItemGroup(
    val itemGroupId: Int,
    val quantity: Int
)

class RecipeViewModel(
    private val recipeDao: RecipeDao,
    private val recipeItemDao: RecipeItemDao,
    private val itemWeeklyPriceDao: ItemWeeklyPriceDao,
    private val appContext: Context,
    private val itemGroupDao: ItemGroupDao,
    private val supermarketDao: SupermarketDao
) : ViewModel() {

    private val _selectedSupermarkets = MutableStateFlow<Set<Int>>(emptySet())
    val selectedSupermarkets: StateFlow<Set<Int>> = _selectedSupermarkets.asStateFlow()

    val allSupermarkets: Flow<List<Supermarket>> = supermarketDao.getAll()

    val recipes: StateFlow<List<RecipeModel>> =
        recipeDao.getAllRecipes()
            .map { list ->
                list.map {
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
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getRecipeById(recipeId: Int): Flow<RecipeModel?> =
        recipeDao.getRecipeById(recipeId).map {
            it?.let { r ->
                RecipeModel(
                    id = r.id,
                    title = r.title,
                    preparationTimeMinutes = r.preparationTimeMinutes,
                    description = r.description,
                    instructions = r.instructions,
                    imagePath = r.imagePath,
                    deletable = r.deletable ?: true
                )
            }
        }

    fun getAllItemGroups(): Flow<List<ItemGroup>> =
        itemGroupDao.getAllItemGroups()

    fun getItemsForRecipeFlow(recipeId: Int): Flow<List<RecipeItem>> =
        recipeItemDao.getItemsForRecipeFlow(recipeId)

    suspend fun getSelectedGroupsForRecipe(recipeId: Int): List<SelectedItemGroup> =
        recipeItemDao.getItemsForRecipe(recipeId).map {
            SelectedItemGroup(it.itemGroupId, it.quantity)
        }

    fun toggleSupermarket(id: Int) {
        val current = _selectedSupermarkets.value
        _selectedSupermarkets.value =
            if (current.contains(id)) current - id else current + id
    }

    fun clearSupermarketFilter() {
        _selectedSupermarkets.value = emptySet()
    }

    fun createRecipe(
        title: String,
        preparationTimeMinutes: Int,
        description: String?,
        instructions: String?,
        imageUri: Uri?,
        selectedGroups: List<SelectedItemGroup>
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

            recipeItemDao.insertAll(
                selectedGroups.map {
                    RecipeItem(recipeId, it.itemGroupId, it.quantity)
                }
            )

            imageUri?.let {
                val path = saveRecipeImage(appContext, it, recipeId)
                recipeDao.updateImagePath(recipeId, path)
            }
        }
    }

    fun updateRecipe(
        recipeId: Int,
        title: String,
        preparationTimeMinutes: Int,
        description: String?,
        instructions: String?,
        imageUri: Uri?,
        selectedGroups: List<SelectedItemGroup>
    ) {
        viewModelScope.launch {
            recipeDao.updateRecipe(
                recipeId,
                title,
                preparationTimeMinutes,
                description ?: "",
                instructions ?: ""
            )

            recipeItemDao.deleteForRecipe(recipeId)
            recipeItemDao.insertAll(
                selectedGroups.map {
                    RecipeItem(recipeId, it.itemGroupId, it.quantity)
                }
            )

            imageUri?.let {
                recipeDao.getRecipeById(recipeId).first()?.imagePath?.let { old ->
                    try { File(old).delete() } catch (_: Exception) {}
                }
                val path = saveRecipeImage(appContext, it, recipeId)
                recipeDao.updateImagePath(recipeId, path)
            }
        }
    }

    fun removeImage(recipeId: Int) {
        viewModelScope.launch {
            recipeDao.getRecipeById(recipeId).first()?.imagePath?.let {
                try { File(it).delete() } catch (_: Exception) {}
            }
            recipeDao.updateImagePath(recipeId, null)
        }
    }

    suspend fun getRecipePriceByPortions(recipeId: Int, portions: Int): Float {
        val items = recipeItemDao.getItemsForRecipe(recipeId)
        val supermarketId = selectedSupermarkets.value.firstOrNull()
        var total = 0f

        for (ri in items) {
            val prices =
                if (supermarketId == null)
                    itemWeeklyPriceDao.getItemSizesAndMinPrices(ri.itemGroupId)
                else
                    itemWeeklyPriceDao.getItemSizesAndMinPricesByStore(
                        ri.itemGroupId,
                        supermarketId
                    )

            var best = Float.MAX_VALUE
            for (p in prices) {
                if (p.size <= 0f) continue
                val needed = ceil((ri.quantity * portions) / p.size).toInt()
                best = minOf(best, needed * p.price)
            }

            if (best != Float.MAX_VALUE) total += best
        }

        return total
    }

    fun getRecipePriceFlow(recipeId: Int): Flow<Float> =
        selectedSupermarkets.flatMapLatest {
            flow { emit(getRecipePriceByPortions(recipeId, 1)) }
        }

    fun getRecipesWithPricesFlow(): Flow<List<Pair<RecipeModel, Float>>> =
        recipes.flatMapLatest { list ->
            if (list.isEmpty()) flowOf(emptyList())
            else combine(
                list.map { recipe ->
                    getRecipePriceFlow(recipe.id).map { recipe to it }
                }
            ) { it.toList().sortedBy { p -> p.second } }
        }

    suspend fun resolveIngredient(
        itemGroupId: Int,
        quantity: Int
    ): Ingredient {

        val group = itemGroupDao.getById(itemGroupId)
            ?: return Ingredient(
                itemGroupId,
                "(unknown)",
                "",
                null,
                null,
                null,
                null,
                quantity
            )

        val supermarketId = selectedSupermarkets.value.firstOrNull()

        val prices =
            if (supermarketId == null)
                itemWeeklyPriceDao.getItemSizesAndMinPrices(itemGroupId)
            else
                itemWeeklyPriceDao.getItemSizesAndMinPricesByStore(
                    itemGroupId,
                    supermarketId
                )

        if (prices.isEmpty()) {
            return Ingredient(
                group.id,
                group.name,
                group.unitType,
                null,
                null,
                null,
                null,
                quantity
            )
        }

        var bestPrice: Float? = null
        var bestItemId: Int? = null
        var bestItemSize: Float? = null

        for (p in prices) {
            if (p.size <= 0f) continue
            val needed = ceil(quantity / p.size).toInt()
            val cost = needed * p.price
            if (bestPrice == null || cost < bestPrice!!) {
                bestPrice = cost
                bestItemId = p.id
                bestItemSize = p.size
            }
        }

        return Ingredient(
            group.id,
            group.name,
            group.unitType,
            bestItemId,
            null,
            bestItemSize,
            bestPrice,
            quantity
        )
    }

    private val _tempGroups = MutableStateFlow<List<SelectedItemGroup>>(emptyList())
    val tempGroups = _tempGroups.asStateFlow()

    fun setTempGroups(groups: List<SelectedItemGroup>) {
        _tempGroups.value = groups
    }

    fun addTempGroup(groupId: Int, qty: Int) {
        _tempGroups.update { list ->
            list.filter { it.itemGroupId != groupId } +
                    SelectedItemGroup(groupId, qty)
        }
    }

    fun removeTempGroup(groupId: Int) {
        _tempGroups.update { list ->
            list.filter { it.itemGroupId != groupId }
        }
    }

    fun clearTempGroups() {
        _tempGroups.value = emptyList()
    }

}
