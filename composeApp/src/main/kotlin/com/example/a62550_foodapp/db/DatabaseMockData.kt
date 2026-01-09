package com.example.a62550_foodapp.db

import android.content.Context
import com.example.a62550_foodapp.R
import com.example.a62550_foodapp.db.entity.*
import com.example.a62550_foodapp.utils.copyDrawableToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Development-only database seeder.
 *
 * - Runs only if database is empty
 * - NOT a migration
 * - Safe to delete at any time
 */
object DatabaseMockData {

    suspend fun populate(
        context: Context,
        database: AppDatabase
    ) = withContext(Dispatchers.IO) {

        // ---- IDEMPOTENT GUARD ----
        if (database.itemGroupDao().count() > 0) return@withContext

        val itemGroupDao = database.itemGroupDao()
        val itemDao = database.itemDao()
        val supermarketDao = database.supermarketDao()
        val itemWeeklyPriceDao = database.itemWeeklyPriceDao()
        val shoppingListDao = database.shoppingListDao()
        val shoppingListItemDao = database.shoppingListItemDao()
        val recipeDao = database.recipeDao()
        val recipeItemDao = database.recipeItemDao()

        // ---- ITEM GROUPS ----
        val onionGroupId = itemGroupDao.insert(
            ItemGroup(
                name = "Onions",
                category = "Fruit & Vegetables",
                unitType = "kg"
            )
        ).toInt()

        val carrotGroupId = itemGroupDao.insert(
            ItemGroup(
                name = "Carrots",
                category = "Fruit & Vegetables",
                unitType = "g"
            )
        ).toInt()

        // ---- SUPERMARKETS ----
        val nettoId = supermarketDao.insert(
            Supermarket(name = "Netto", logo = null)
        ).toInt()

        val kvicklyId = supermarketDao.insert(
            Supermarket(name = "Kvickly", logo = null)
        ).toInt()

        // ---- ITEMS ----
        val onionItemId = itemDao.insert(
            Item(
                itemGroupId = onionGroupId,
                name = "Yellow onions",
                size = 1f,
                unitType = "kg",
                imagePath = null
            )
        ).toInt()

        val carrotItemId = itemDao.insert(
            Item(
                itemGroupId = carrotGroupId,
                name = "Carrots",
                size = 500f,
                unitType = "g",
                imagePath = null
            )
        ).toInt()

        // ---- ITEM WEEKLY PRICES ----
        val year = 2025
        val week = 1

        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = onionItemId,
                year = year,
                week = week,
                price = 12.0f,
                supermarket_id = nettoId
            )
        )

        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = carrotItemId,
                year = year,
                week = week,
                price = 7.5f,
                supermarket_id = nettoId
            )
        )

        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = carrotItemId,
                year = year,
                week = week,
                price = 8.5f,
                supermarket_id = kvicklyId
            )
        )

        // ---- SHOPPING LIST ----
        val shoppingListId = shoppingListDao.insert(
            ShoppingList(
                name = "Dinner",
                lastPriceCheckTimestamp = System.currentTimeMillis()
            )
        ).toInt()

        shoppingListItemDao.insert(
            ShoppingListItem(
                shoppingListId = shoppingListId,
                itemId = onionItemId,
                calcQuantity = 2f,
                isChecked = false
            )
        )

        shoppingListItemDao.insert(
            ShoppingListItem(
                shoppingListId = shoppingListId,
                itemId = carrotItemId,
                calcQuantity = 1f,
                isChecked = false
            )
        )

        // ---- RECIPES ----
        val recipes = listOf(
            Triple("Spaghetti Bolognese", 45, R.drawable.recipe_1),
            Triple("Chicken curry", 40, R.drawable.recipe_2),
            Triple("Lasagna", 60, R.drawable.recipe_3)
        )

        val recipeIds = recipes.mapIndexed { index, (title, time, drawableRes) ->
            val imagePath = copyDrawableToInternalStorage(
                context = context,
                drawableRes = drawableRes,
                targetFileName = "recipe_mock_${index + 1}.webp"
            )

            recipeDao.insert(
                Recipe(
                    title = title,
                    preparationTimeMinutes = time,
                    description = "Mock description",
                    instructions = "Mock instructions",
                    imagePath = imagePath,
                    deletable = false
                )
            ).toInt()
        }

        // ---- RECIPE ITEMS (USES ITEM GROUPS) ----
        recipeItemDao.insert(
            RecipeItem(
                recipeId = recipeIds[0],
                itemGroupId = onionGroupId,
                quantity = 1f
            )
        )

        recipeItemDao.insert(
            RecipeItem(
                recipeId = recipeIds[0],
                itemGroupId = carrotGroupId,
                quantity = 1f
            )
        )
    }
}
