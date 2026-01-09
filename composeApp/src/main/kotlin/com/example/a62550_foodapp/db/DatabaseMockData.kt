package com.example.a62550_foodapp.db

import android.content.Context
import com.example.a62550_foodapp.R
import com.example.a62550_foodapp.db.entity.*
import com.example.a62550_foodapp.utils.copyDrawableToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Inserts local mock data for development and demo purposes.
 *
 * - Runs only if database is empty (idempotent)
 * - Not used in production
 * - Not a migration
 */
object DatabaseMockData {

    suspend fun populate(
        context: Context,
        database: AppDatabase
    ) = withContext(Dispatchers.IO) {

        // ---- IDEMPOTENT GUARD ----
        if (database.itemDao().count() > 0) return@withContext

        val itemGroupDao = database.itemGroupDao()
        val itemDao = database.itemDao()
        val supermarketDao = database.supermarketDao()
        val shoppingListDao = database.shoppingListDao()
        val shoppingListItemDao = database.shoppingListItemDao()
        val itemWeeklyPriceDao = database.itemWeeklyPriceDao()
        val recipeDao = database.recipeDao()
        val recipeItemDao = database.recipeItemDao()

        // ---- ITEM GROUPS ----
        val onionGroupId = itemGroupDao.insert(
            ItemGroup(
                name = "Løg",
                category = "Frugt & grønt",
                unitType = "kg"
            )
        ).toInt()

        val carrotGroupId = itemGroupDao.insert(
            ItemGroup(
                name = "Gulerødder",
                category = "Frugt & grønt",
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

        // ---- ITEMS (concrete products) ----
        val onionItemId = itemDao.insert(
            Item(
                itemGroupId = onionGroupId,
                name = "Løg",
                size = 1f,
                unitType = "kg",
                imagePath = null
            )
        ).toInt()

        val carrotItemId = itemDao.insert(
            Item(
                itemGroupId = carrotGroupId,
                name = "Gulerødder",
                size = 500f,
                unitType = "g",
                imagePath = null
            )
        ).toInt()

        // ---- ITEM WEEKLY PRICES ----
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = onionItemId,
                year = 2024,
                week = 28,
                price = 12.0f,
                supermarket_id = nettoId
            )
        )

        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = carrotItemId,
                year = 2024,
                week = 28,
                price = 7.0f,
                supermarket_id = nettoId
            )
        )

        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = carrotItemId,
                year = 2024,
                week = 28,
                price = 8.5f,
                supermarket_id = kvicklyId
            )
        )

        // ---- SHOPPING LIST ----
        val listId = shoppingListDao.insert(
            ShoppingList(name = "Aftensmad")
        ).toInt()

        shoppingListItemDao.insert(
            ShoppingListItem(
                shoppingListId = listId,
                itemId = onionItemId,
                calcQuantity = 2f,
                isChecked = false
            )
        )

        // ---- RECIPES ----
        val recipes = listOf(
            Triple("Spaghetti Bolognese", 45, R.drawable.recipe_1),
            Triple("Kylling i karry", 40, R.drawable.recipe_2),
            Triple("Lasagne", 60, R.drawable.recipe_3),
            Triple("Pasta Alfredo", 30, R.drawable.recipe_4),
            Triple("Chili con carne", 55, R.drawable.recipe_5),
            Triple("Fried rice", 25, R.drawable.recipe_6),
            Triple("Burger", 35, R.drawable.recipe_7),
            Triple("Salat med kylling", 20, R.drawable.recipe_8)
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

        // Spaghetti Bolognese
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

        // Kylling i karry
        recipeItemDao.insert(
            RecipeItem(
                recipeId = recipeIds[1],
                itemGroupId = onionGroupId,
                quantity = 2f
            )
        )

        // Lasagne
        recipeItemDao.insert(
            RecipeItem(
                recipeId = recipeIds[2],
                itemGroupId = carrotGroupId,
                quantity = 1f
            )
        )
    }
}
