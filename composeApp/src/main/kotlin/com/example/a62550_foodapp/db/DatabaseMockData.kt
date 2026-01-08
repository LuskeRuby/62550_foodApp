package com.example.a62550_foodapp.db

import android.content.Context
import com.example.a62550_foodapp.db.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.a62550_foodapp.R
import com.example.a62550_foodapp.db.entity.Recipe
import com.example.a62550_foodapp.utils.copyDrawableToInternalStorage


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
        if (database.itemDao().count() > 0) {
            return@withContext
        }

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
        val carrotsId = itemDao.insert(
            Item(name = "Gulerødder", unit = "g", itemgroup = 1, picture = null)
        ).toInt()

        val onionId = itemDao.insert(
            Item(name = "Løg", unit = "stk", itemgroup = 1, picture = null)
        ).toInt()

        val garlicId = itemDao.insert(
            Item(name = "Hvidløg", unit = "fed", itemgroup = 1, picture = null)
        ).toInt()

        val oilId = itemDao.insert(
            Item(name = "Olie", unit = "spsk", itemgroup = 1, picture = null)
        ).toInt()

        val bouillonId = itemDao.insert(
            Item(name = "Grøntsagsbouillon", unit = "dl", itemgroup = 1, picture = null)
        ).toInt()

        val pastaId = itemDao.insert(
            Item(name = "Pasta", unit = "g", itemgroup = 2, picture = null)
        ).toInt()

        val tomatoId = itemDao.insert(
            Item(name = "Hakkede tomater", unit = "dåse", itemgroup = 1, picture = null)
        ).toInt()

        // ---- ITEM PRICES ----
        itemWeeklyPriceDao.insert(ItemWeeklyPrice(item_id = carrotsId, year = 2024, week = 28, price = 0.02f, supermarket_id = nettoId)) // 0.02 kr per g
        itemWeeklyPriceDao.insert(ItemWeeklyPrice(item_id = onionId, year = 2024, week = 28, price = 2.0f, supermarket_id = nettoId)) // 2 kr per stk
        itemWeeklyPriceDao.insert(ItemWeeklyPrice(item_id = garlicId, year = 2024, week = 28, price = 1.5f, supermarket_id = nettoId)) // 1.5 kr per fed
        itemWeeklyPriceDao.insert(ItemWeeklyPrice(item_id = oilId, year = 2024, week = 28, price = 0.5f, supermarket_id = nettoId)) // 0.5 kr per spsk
        itemWeeklyPriceDao.insert(ItemWeeklyPrice(item_id = bouillonId, year = 2024, week = 28, price = 1.0f, supermarket_id = nettoId)) // 1.0 kr per dl
        itemWeeklyPriceDao.insert(ItemWeeklyPrice(item_id = pastaId, year = 2024, week = 28, price = 0.03f, supermarket_id = nettoId)) // 0.03 kr per g
        itemWeeklyPriceDao.insert(ItemWeeklyPrice(item_id = tomatoId, year = 2024, week = 28, price = 10.0f, supermarket_id = nettoId)) // 10 kr per dåse

        // ---- RECIPES ----
        
        // 1. Gulerodssuppe
        val carrotSoupId = recipeDao.insert(
            Recipe(
                title = "Gulerodssuppe",
                description = "En cremet og lækker suppe",
                instructions = "Svits grøntsagerne i olie. Tilsæt bouillon og lad simre.",
                imagePath = copyDrawableToInternalStorage(context, R.drawable.recipe_1, "gulerodssuppe.webp"),
                deletable = false
            )
        ).toInt()

        recipeItemDao.insert(RecipeItem(recipe_id = carrotSoupId, item_id = carrotsId, quantity = 500f)) // 500 * 0.02 = 10
        recipeItemDao.insert(RecipeItem(recipe_id = carrotSoupId, item_id = onionId, quantity = 1f)) // 1 * 2 = 2
        recipeItemDao.insert(RecipeItem(recipe_id = carrotSoupId, item_id = oilId, quantity = 2f)) // 2 * 0.5 = 1
        recipeItemDao.insert(RecipeItem(recipe_id = carrotSoupId, item_id = bouillonId, quantity = 7f)) // 7 * 1 = 7
        // Total Gulerodssuppe = 10 + 2 + 1 + 7 = 20kr

        // 2. Simpel Pasta
        val pastaRecipeId = recipeDao.insert(
            Recipe(
                title = "Simpel Pasta",
                description = "Hurtig hverdagsmad",
                instructions = "Kog pastaen. Opvarm tomaterne og bland det hele sammen.",
                imagePath = copyDrawableToInternalStorage(context, R.drawable.recipe_4, "simpel_pasta.webp"),
                deletable = false
            )
        ).toInt()

        recipeItemDao.insert(RecipeItem(recipe_id = pastaRecipeId, item_id = pastaId, quantity = 250f)) // 250 * 0.03 = 7.5
        recipeItemDao.insert(RecipeItem(recipe_id = pastaRecipeId, item_id = tomatoId, quantity = 1f)) // 1 * 10 = 10
        recipeItemDao.insert(RecipeItem(recipe_id = pastaRecipeId, item_id = garlicId, quantity = 2f)) // 2 * 1.5 = 3
        // Total Simpel Pasta = 7.5 + 10 + 3 = 20.5kr

        // Populate other mock recipes without items (will have 0 price)
        val otherRecipes = listOf(
            "Kylling i karry" to R.drawable.recipe_2,
            "Lasagne" to R.drawable.recipe_3,
            "Chili con carne" to R.drawable.recipe_5,
            "Fried rice" to R.drawable.recipe_6,
            "Burger" to R.drawable.recipe_7,
            "Salat med kylling" to R.drawable.recipe_8
        )

        otherRecipes.forEachIndexed { index, (title, drawableRes) ->
            val imagePath = copyDrawableToInternalStorage(
                context = context,
                drawableRes = drawableRes,
                targetFileName = "recipe_mock_${index + 1}.webp"
            )

            recipeDao.insert(
                Recipe(
                    title = title,
                    description = "Mock description",
                    instructions = "Mock instructions",
                    imagePath = imagePath,
                    deletable = false
                )
            )
        }
    }
}
