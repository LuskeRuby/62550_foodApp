package com.example.a62550_foodapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a62550_foodapp.db.dao.*
import com.example.a62550_foodapp.db.entity.RecipeItem
import com.example.a62550_foodapp.db.entity.Recipe as RecipeEntity
import com.example.a62550_foodapp.db.entity.ItemGroup
import com.example.a62550_foodapp.db.entity.ShoppingListItemGroup
import com.example.a62550_foodapp.db.entity.Supermarket
import com.example.a62550_foodapp.model.Recipe as RecipeModel
import com.example.a62550_foodapp.utils.saveRecipeImage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.ceil

/**
 * Lightweight DTO used while creating/editing recipes before saving.
 */
data class SelectedItemGroup(
    val itemGroupId: Long,
    val quantity: Int
)

/**
 * ViewModel responsible for:
 * - CRUD operations on recipes
 * - Managing recipe ingredients
 * - Calculating recipe prices based on selected supermarkets
 * - Adding recipe ingredients to shopping list
 *
 * IMPORTANT:
 * This ViewModel does NOT store supermarket filter state.
 * The selected stores are provided by UI via StoreFilterViewModel
 * and passed into price-calculation functions as parameters.
 */
class RecipeViewModel(
    private val recipeDao: RecipeDao,
    private val recipeItemDao: RecipeItemDao,
    private val itemWeeklyPriceDao: ItemWeeklyPriceDao,
    private val appContext: Context,
    private val itemGroupDao: ItemGroupDao,
    private val supermarketDao: SupermarketDao,
    private val shoppingListItemGroupDao: ShoppingListItemGroupDao
) : ViewModel() {

    /* -------------------------------------------------------------------------
     * BASIC DATA FLOWS
     * ------------------------------------------------------------------------- */

    /** Flow of all supermarkets (used in filter UI). */
    val allSupermarkets: Flow<List<Supermarket>> = supermarketDao.getAll()

    /** Flow of all recipes mapped to UI model. */
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

    /** Returns a single recipe as Flow for detail screen. */
    fun getRecipeById(recipeId: Long): Flow<RecipeModel?> =
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

    /** Returns all ingredient groups for selection when creating/editing recipes. */
    fun getAllItemGroups(): Flow<List<ItemGroup>> =
        itemGroupDao.getAllItemGroups()

    /** Reactive ingredient list for recipe detail/edit screens. */
    fun getItemsForRecipeFlow(recipeId: Long): Flow<List<RecipeItem>> =
        recipeItemDao.getItemsForRecipeFlow(recipeId)

    /** Non-reactive ingredient list used for calculations. */
    suspend fun getSelectedGroupsForRecipe(recipeId: Long): List<SelectedItemGroup> =
        recipeItemDao.getItemsForRecipe(recipeId).map {
            SelectedItemGroup(it.itemGroupId, it.sizeOfOnePortion)
        }

    /* -------------------------------------------------------------------------
     * CREATE / UPDATE RECIPES
     * ------------------------------------------------------------------------- */

    /**
     * Creates a new recipe and its ingredient mappings.
     * Optionally saves an image to internal storage.
     */
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
            )

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

    /**
     * Updates an existing recipe and replaces all its ingredients.
     */
    fun updateRecipe(
        recipeId: Long,
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

    /** Removes recipe image file and clears DB reference. */
    fun removeImage(recipeId: Long) {
        viewModelScope.launch {
            recipeDao.getRecipeById(recipeId).first()?.imagePath?.let {
                try { File(it).delete() } catch (_: Exception) {}
            }
            recipeDao.updateImagePath(recipeId, null)
        }
    }

    /* -------------------------------------------------------------------------
     * PRICE CALCULATION (USES STORE FILTER FROM UI)
     * ------------------------------------------------------------------------- */

    /**
     * Calculates the total recipe price for a given number of portions.
     */
    suspend fun getRecipePriceByPortions(
        recipeId: Long,
        portions: Int,
        selectedStores: Set<Long>
    ): Float {

        val items = recipeItemDao.getItemsForRecipe(recipeId)
        var total = 0f

        for (ri in items) {

            val prices =
                if (selectedStores.isEmpty())
                    itemWeeklyPriceDao.getItemSizesAndMinPrices(ri.itemGroupId)
                else
                    itemWeeklyPriceDao.getItemSizesAndMinPricesByStores(
                        ri.itemGroupId,
                        selectedStores.toList()
                    )

            var best = Float.MAX_VALUE

            for (p in prices) {
                if (p.size <= 0f) continue
                val needed = ceil((ri.sizeOfOnePortion * portions) / p.size).toInt()
                best = minOf(best, needed * p.price)
            }

            if (best != Float.MAX_VALUE) total += best
        }

        return total
    }

    /**
     * Returns reactive list of recipes with calculated prices.
     */
    fun getRecipesWithPricesFlow(
        selectedStores: Set<Long>,
        portions: Int
    ): Flow<List<Pair<RecipeModel, Float>>> =
        recipes
            .flatMapLatest { list ->
                flow {
                    val result = list.map { recipe ->
                        val price = getRecipePriceByPortions(
                            recipeId = recipe.id,
                            portions = portions,
                            selectedStores = selectedStores
                        )
                        recipe to price
                    }.sortedBy { it.second }

                    emit(result)
                }
            }
            .flowOn(kotlinx.coroutines.Dispatchers.IO)

    /* -------------------------------------------------------------------------
     * TEMP INGREDIENT SELECTION (CREATE / EDIT SCREEN)
     * ------------------------------------------------------------------------- */

    // ---- EDIT STATE (UI FORM STATE) ----

    private val _editTitle = MutableStateFlow("")
    val editTitle = _editTitle.asStateFlow()

    private val _editTime = MutableStateFlow("")
    val editTime = _editTime.asStateFlow()

    private val _editDescription = MutableStateFlow<String?>(null)
    val editDescription = _editDescription.asStateFlow()

    private val _editInstructions = MutableStateFlow<String?>(null)
    val editInstructions = _editInstructions.asStateFlow()

    private val _editImagePath = MutableStateFlow<String?>(null)
    val editImagePath = _editImagePath.asStateFlow()

    private val _editImageUri = MutableStateFlow<Uri?>(null)
    val editImageUri = _editImageUri.asStateFlow()

    private val _ingredientsLoadedForEdit = MutableStateFlow(false)

    private val _tempGroups = MutableStateFlow<List<SelectedItemGroup>>(emptyList())
    val tempGroups = _tempGroups.asStateFlow()

    fun ingredientsLoaded(): Boolean =
        _ingredientsLoadedForEdit.value

    fun markIngredientsLoaded() {
        _ingredientsLoadedForEdit.value = true
    }

    fun setTempGroups(groups: List<SelectedItemGroup>) {
        _tempGroups.value = groups
    }

    fun addTempGroup(groupId: Long, qty: Int) {
        _tempGroups.update { list ->
            list.filter { it.itemGroupId != groupId } +
                    SelectedItemGroup(groupId, qty)
        }
    }

    fun removeTempGroup(groupId: Long) {
        _tempGroups.update { list ->
            list.filter { it.itemGroupId != groupId }
        }
    }

    fun clearTempGroups() {
        _tempGroups.value = emptyList()
    }

    fun resetEditState() {
        _ingredientsLoadedForEdit.value = false
        _tempGroups.value = emptyList()

        _editTitle.value = ""
        _editTime.value = ""
        _editDescription.value = null
        _editInstructions.value = null
        _editImagePath.value = null
        _editImageUri.value = null
    }

    // setters for ui model
    fun setEditTitle(v: String) { _editTitle.value = v }
    fun setEditTime(v: String) { _editTime.value = v }
    fun setEditDescription(v: String?) { _editDescription.value = v }
    fun setEditInstructions(v: String?) { _editInstructions.value = v }
    fun setEditImageUri(v: Uri?) { _editImageUri.value = v }
    fun setEditImagePath(v: String?) { _editImagePath.value = v }

    //load funktion
    suspend fun loadRecipeForEdit(recipeId: Long) {
        if (_ingredientsLoadedForEdit.value) return

        val recipe = recipeDao.getRecipeById(recipeId).first() ?: return

        _editTitle.value = recipe.title
        _editTime.value = recipe.preparationTimeMinutes.toString()
        _editDescription.value = recipe.description
        _editInstructions.value = recipe.instructions
        _editImagePath.value = recipe.imagePath
        _editImageUri.value = null

        _tempGroups.value = getSelectedGroupsForRecipe(recipeId)

        _ingredientsLoadedForEdit.value = true
    }


    /* -------------------------------------------------------------------------
     * SHOPPING LIST INTEGRATION
     * ------------------------------------------------------------------------- */

    /**
     * Adds all ingredients (itemGroups) of a recipe to the shopping list.
     */
    fun addRecipeToShoppingList(
        shoppingListId: Long,
        recipeId: Long,
        portions: Int
    ) {
        viewModelScope.launch {

            // Guard: prevent duplicate recipe inserts
            val alreadyExists =
                shoppingListItemGroupDao.recipeExistsInList(
                    shoppingListId = shoppingListId,
                    recipeId = recipeId
                )

            if (alreadyExists) return@launch

            val recipeItems = recipeItemDao.getItemsForRecipe(recipeId)

            if (recipeItems.isEmpty()) return@launch

            val rows = recipeItems.map { recipeItem ->
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId,
                    itemGroupId = recipeItem.itemGroupId,
                    recipeId = recipeId,
                    portionQuantity = portions,
                    portionSize = recipeItem.sizeOfOnePortion.toFloat(),
                    isChecked = false
                )
            }

            shoppingListItemGroupDao.insert(rows)
        }
    }


    suspend fun resolveIngredients(
        items: List<RecipeItem>,
        portions: Int
    ): List<Triple<String, Int, String>> {

        val groups = itemGroupDao.getAllItemGroups().first()

        return items.mapNotNull { ri ->
            val group = groups.firstOrNull { it.id == ri.itemGroupId } ?: return@mapNotNull null

            Triple(
                group.name,
                ri.sizeOfOnePortion * portions,
                group.unitType
            )
        }
    }

}