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

        // ---- SUPERMARKETS ----
        val nettoId = supermarketDao.insert(
            Supermarket(name = "Netto", logo = null)
        )

        val kvicklyId = supermarketDao.insert(
            Supermarket(name = "Kvickly", logo = null)
        )

        // ---- ITEMS ----
        val carrotsId = itemDao.insert(
            Item(name = "Gulerødder", unit = "500g", itemgroup = 1, picture = null)
        )

        val onionId = itemDao.insert(
            Item(name = "Løg", unit = "1 kg", itemgroup = 1, picture = null)
        )

        val creamId = itemDao.insert(
            Item(name = "Fløde", unit = "2,5 dl", itemgroup = 2, picture = null)
        )

        val bouillonId = itemDao.insert(
            Item(name = "Grøntsagsbouillon", unit = "100g", itemgroup = 3, picture = null)
        )

        val oilId = itemDao.insert(
            Item(name = "Olie", unit = "500ml", itemgroup = 3, picture = null)
        )


        // ---- ITEM PRICES ----

        // Gulerødder
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(carrotsId.toInt(), 2024, 28, 7.0f, nettoId.toInt())
        )
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(carrotsId.toInt(), 2024, 28, 8.5f, kvicklyId.toInt())
        )

        // Løg
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(onionId.toInt(), 2024, 28, 12.0f, nettoId.toInt())
        )
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(onionId.toInt(), 2024, 28, 13.0f, kvicklyId.toInt())
        )

        // Fløde (Mejeri)
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(creamId.toInt(), 2024, 28, 16.0f, nettoId.toInt())
        )
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(creamId.toInt(), 2024, 28, 15.0f, kvicklyId.toInt())
        )

        // Grøntsagsbouillon
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(bouillonId.toInt(), 2024, 28, 5.0f, nettoId.toInt())
        )
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(bouillonId.toInt(), 2024, 28, 6.0f, kvicklyId.toInt())
        )

        // Olie
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(oilId.toInt(), 2024, 28, 18.0f, nettoId.toInt())
        )
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(oilId.toInt(), 2024, 28, 17.0f, kvicklyId.toInt())
        )

        // ---- SHOPPING LIST ----
        val listId = shoppingListDao.insert(
            ShoppingList(name = "Aftensmad")
        )
        shoppingListItemDao.insert(
            ShoppingListItem(
                shopping_list_id = listId.toInt(),
                item_id = creamId.toInt(),
                quantity = 1.0f,
                is_checked = false,
                label = ""
            )
        )

        shoppingListItemDao.insert(
            ShoppingListItem(
                shopping_list_id = listId.toInt(),
                item_id = bouillonId.toInt(),
                quantity = 1.0f,
                is_checked = false,
                label = ""
            )
        )

        shoppingListItemDao.insert(
            ShoppingListItem(
                shopping_list_id = listId.toInt(),
                item_id = oilId.toInt(),
                quantity = 1.0f,
                is_checked = false,
                label = ""
            )
        )



        // ---- RECIPES ----
        val recipes = listOf(
            "Spaghetti Bolognese" to R.drawable.recipe_1,
            "Kylling i karry" to R.drawable.recipe_2,
            "Lasagne" to R.drawable.recipe_3,
            "Pasta Alfredo" to R.drawable.recipe_4,
            "Chili con carne" to R.drawable.recipe_5,
            "Fried rice" to R.drawable.recipe_6,
            "Burger" to R.drawable.recipe_7,
            "Salat med kylling" to R.drawable.recipe_8
        )

        recipes.forEachIndexed { index, (title, drawableRes) ->

            val imagePath = copyDrawableToInternalStorage(
                context = context,
                drawableRes = drawableRes,
                targetFileName = "recipe_mock_${index + 1}.webp"
            )

            database.recipeDao().insert(
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
