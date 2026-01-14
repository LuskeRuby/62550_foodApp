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
import com.example.a62550_foodapp.db.dao.SupermarketDao
import com.example.a62550_foodapp.db.entity.Recipe as RecipeEntity
import com.example.a62550_foodapp.db.entity.Supermarket
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
    private val itemGroupDao: ItemGroupDao,
    private val supermarketDao: SupermarketDao
) : ViewModel() {

    // Track selected supermarkets for filtering
    private val _selectedSupermarkets = MutableStateFlow<Set<Int>>(emptySet())
    val selectedSupermarkets: StateFlow<Set<Int>> = _selectedSupermarkets.asStateFlow()

    // Get all available supermarkets
    val allSupermarkets: Flow<List<Supermarket>> = supermarketDao.getAll()

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
    fun getItemsForRecipeFlow(recipeId: Int): Flow<List<RecipeItem>> =
        recipeItemDao.getItemsForRecipeFlow(recipeId)

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
                val items = selectedGroups.map { sg ->
                    RecipeItem(recipeId, sg.itemGroupId, sg.quantity)
                }

                try {
                    recipeItemDao.insertAll(items)
                } catch (_: Exception) {
                    // ignore failures for now (minimal change)
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

    fun toggleSupermarket(supermarketId: Int) {
        val current = _selectedSupermarkets.value
        _selectedSupermarkets.value = if (current.contains(supermarketId)) {
            current - supermarketId
        } else {
            current + supermarketId
        }
    }

    fun clearSupermarketFilter() {
        _selectedSupermarkets.value = emptySet()
    }

    /**
     * Calculate the total price of a recipe by finding the cheapest way to buy each ingredient.
     * For each ingredient, considers all available package sizes and calculates the total cost
     * of buying enough packages to meet the recipe's requirements.
     *
     * Example: Recipe needs 500g beef
     * - Option 1: 400g @ 50kr (need 2 packs) = 100kr total
     * - Option 2: 600g @ 70kr (need 1 pack) = 70kr total
     * Result: Picks option 2 @ 70kr (cheapest to buy enough)
     *
     * @param recipeId The ID of the recipe
     * @return The total price of the recipe, or 0f if no prices are found
     */
    suspend fun getRecipePrice(recipeId: Int): Float {
        return try {
            val recipeItems = recipeItemDao.getItemsForRecipe(recipeId)
            if (recipeItems.isEmpty()) {
                return 0f
            }

            var totalPrice = 0f
            for (recipeItem in recipeItems) {
                val itemPrices = itemWeeklyPriceDao.getItemSizesAndMinPrices(recipeItem.itemGroupId)
                if (itemPrices.isEmpty()) continue

                // Find the cheapest way to buy enough of this ingredient
                var cheapestCost = Float.MAX_VALUE
                for (item in itemPrices) {
                    if (item.size > 0) {
                        // Calculate how many packages we need
                        val packagesNeeded = kotlin.math.ceil(recipeItem.quantity / item.size).toInt()
                        val totalCost = packagesNeeded * item.price
                        cheapestCost = minOf(cheapestCost, totalCost.toFloat())
                    }
                }

                if (cheapestCost != Float.MAX_VALUE && cheapestCost > 0) {
                    totalPrice += cheapestCost
                }
            }
            totalPrice
        } catch (_: Exception) {
            0f
        }
    }

    fun getRecipePriceFlow(recipeId: Int): Flow<Float> {
        return combine(
            recipeItemDao.getItemsForRecipeFlow(recipeId),
            _selectedSupermarkets
        ) { recipeItems, selectedSupermarkets ->
            if (recipeItems.isEmpty()) {
                0f
            } else {
                combine(
                    recipeItems.map { recipeItem ->
                        // Use filtered prices if supermarkets are selected, otherwise use all prices
                        val pricesFlow = if (selectedSupermarkets.isEmpty()) {
                            itemWeeklyPriceDao.getItemSizesAndMinPricesFlow(recipeItem.itemGroupId)
                        } else {
                            itemWeeklyPriceDao.getItemSizesAndMinPricesByStoresFlow(
                                recipeItem.itemGroupId,
                                selectedSupermarkets.toList()
                            )
                        }

                        pricesFlow.map { itemPrices ->
                            if (itemPrices.isEmpty()) return@map 0f

                            var cheapestCost = Float.MAX_VALUE

                            for (item in itemPrices) {
                                if (item.size > 0f) {
                                    val packagesNeeded =
                                        kotlin.math.ceil(recipeItem.quantity / item.size).toInt()
                                    val totalCost = packagesNeeded * item.price
                                    cheapestCost = minOf(cheapestCost, totalCost.toFloat())
                                }
                            }

                            if (cheapestCost != Float.MAX_VALUE && cheapestCost > 0f)
                                cheapestCost
                            else
                                0f
                        }
                    }
                ) { ingredientCosts ->
                    ingredientCosts.sum()
                }.first()   // wait for combined emission
            }
        }.mapLatest { it }
    }

    fun getRecipesWithPricesFlow(): Flow<List<Pair<RecipeModel, Float>>> {

        return recipes.mapLatest { recipeList ->

            if (recipeList.isEmpty()) {
                emptyList()
            } else {

                combine(
                    recipeList.map { recipe ->
                        getRecipePriceFlow(recipe.id)
                            .map { price -> recipe to price }
                    }
                ) { recipePairs ->
                    recipePairs
                        .toList()
                        .sortedBy { it.second }
                }.first()   // wait for combined result
            }
        }
    }

    /**
     * Calculate the price of a recipe based on the portion size.
     * Scales the ingredient quantities by the portion multiplier and calculates the total cost.
     *
     * @param recipeId The ID of the recipe
     * @param portions The number of portions
     * @return The total price for the given number of portions, or 0f if no price is found
     */
    suspend fun getRecipePriceByPortions(recipeId: Int, portions: Int): Float {
        return try {
            val recipeItems = recipeItemDao.getItemsForRecipe(recipeId)
            if (recipeItems.isEmpty()) {
                return 0f
            }

            var totalPrice = 0f
            for (recipeItem in recipeItems) {
                // Scale the quantity by the number of portions
                val scaledQuantity = recipeItem.quantity * portions

                val itemPrices = itemWeeklyPriceDao.getItemSizesAndMinPrices(recipeItem.itemGroupId)
                if (itemPrices.isEmpty()) continue

                // Find the cheapest way to buy enough of this ingredient
                var cheapestCost = Float.MAX_VALUE
                for (item in itemPrices) {
                    if (item.size > 0) {
                        // Calculate how many packages we need based on scaled quantity
                        val packagesNeeded = kotlin.math.ceil(scaledQuantity / item.size).toInt()
                        val totalCost = packagesNeeded * item.price
                        cheapestCost = minOf(cheapestCost, totalCost.toFloat())
                    }
                }

                if (cheapestCost != Float.MAX_VALUE && cheapestCost > 0) {
                    totalPrice += cheapestCost
                }
            }
            totalPrice
        } catch (_: Exception) {
            0f
        }
    }
}
