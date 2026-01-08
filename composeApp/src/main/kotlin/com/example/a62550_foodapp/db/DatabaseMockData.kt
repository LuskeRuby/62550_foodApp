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

        val itemDao = database.itemDao()
        val supermarketDao = database.supermarketDao()
        val shoppingListDao = database.shoppingListDao()
        val shoppingListItemDao = database.shoppingListItemDao()
        val itemWeeklyPriceDao = database.itemWeeklyPriceDao()
        val recipeDao = database.recipeDao()
        val recipeItemDao = database.recipeItemDao()

        // ---- SUPERMARKETS ----
        val nettoId = supermarketDao.insert(
            Supermarket(name = "Netto", logo = null)
        ).toInt()

        val kvicklyId = supermarketDao.insert(
            Supermarket(name = "Kvickly", logo = null)
        ).toInt()

        // ---- ITEMS ----
        // itemGroupId = 1 → Løg
        val onionId = itemDao.insert(
            Item(
                itemGroupId = 1,
                category = "Frugt & grønt",
                name = "løg",
                size = 1f,
                unitType = "kg",
                imagePath = null
            )
        ).toInt()

        // itemGroupId = 2 → Gulerødder
        val carrotsId = itemDao.insert(
            Item(
                itemGroupId = 2,
                category = "Frugt & grønt",
                name = "Gulerødder",
                size = 500f,
                unitType = "g",
                imagePath = null
            )
        ).toInt()

        // ---- ITEM PRICES ----
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = onionId,
                year = 2024,
                week = 28,
                price = 12.0f,
                supermarket_id = nettoId
            )
        )

        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = carrotsId,
                year = 2024,
                week = 28,
                price = 7.0f,
                supermarket_id = nettoId
            )
        )

        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = carrotsId,
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
                itemId = onionId,
                quantity = 2f,
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

        // ---- RECIPE ITEMS ----

// Spaghetti Bolognese
        recipeItemDao.insert(
            RecipeItem(
                recipe_id = recipeIds[0],
                item_group_id = 1, // Løg
                quantity = 1f
            )
        )
        recipeItemDao.insert(
            RecipeItem(
                recipe_id = recipeIds[0],
                item_group_id = 2, // Gulerødder
                quantity = 1f
            )
        )

// Kylling i karry
        recipeItemDao.insert(
            RecipeItem(
                recipe_id = recipeIds[1],
                item_group_id = 1, // Løg
                quantity = 2f
            )
        )

// Lasagne
        recipeItemDao.insert(
            RecipeItem(
                recipe_id = recipeIds[2],
                item_group_id = 2, // Gulerødder
                quantity = 1f
            )
        )
    }
}