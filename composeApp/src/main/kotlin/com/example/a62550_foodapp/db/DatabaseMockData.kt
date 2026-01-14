package com.example.a62550_foodapp.db

import android.content.Context
import com.example.a62550_foodapp.R
import com.example.a62550_foodapp.db.entity.*
import com.example.a62550_foodapp.utils.copyDrawableToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        val recipeDao = database.recipeDao()
        val recipeItemDao = database.recipeItemDao()
        val shoppingListDao = database.shoppingListDao()
        val shoppingListItemDao = database.shoppingListItemDao()

        val year = 2025
        val week = 1

        /* ---------- SUPERMARKETS ---------- */

        val nettoId = supermarketDao.insert(
            Supermarket(name = "Netto", logo = null)
        ).toInt()

        val kvicklyId = supermarketDao.insert(
            Supermarket(name = "Kvickly", logo = null)
        ).toInt()

        /* ---------- ITEM GROUPS ---------- */

        val pastaGroupId = itemGroupDao.insert(
            ItemGroup(name = "Pasta", category = "Tørvarer", unitType = "g")
        ).toInt()

        val beefGroupId = itemGroupDao.insert(
            ItemGroup(name = "Hakket oksekød", category = "Kød", unitType = "g")
        ).toInt()

        val onionGroupId = itemGroupDao.insert(
            ItemGroup(name = "Løg", category = "Grøntsager", unitType = "g")
        ).toInt()

        val chickenGroupId = itemGroupDao.insert(
            ItemGroup(name = "Kyllingebryst", category = "Kød", unitType = "g")
        ).toInt()

        val pepperGroupId = itemGroupDao.insert(
            ItemGroup(name = "Peberfrugt", category = "Grøntsager", unitType = "g")
        ).toInt()

        val soyGroupId = itemGroupDao.insert(
            ItemGroup(name = "Sojasauce", category = "Kolonial", unitType = "ml")
        ).toInt()

        /* ---------- ITEMS ---------- */

        val items = listOf(
            Item(
                id = 0,
                itemGroupId = pastaGroupId,
                name = "Spaghetti 500 g",
                size = 500f,
                unitType = "g",
                imagePath = null
            ),
            Item(
                id = 0,
                itemGroupId = pastaGroupId,
                name = "Penne 1 kg",
                size = 1000f,
                unitType = "g",
                imagePath = null
            ),
            Item(
                id = 0,
                itemGroupId = beefGroupId,
                name = "Hakket oksekød 8% 400 g",
                size = 400f,
                unitType = "g",
                imagePath = null
            ),
            Item(
                id = 0,
                itemGroupId = beefGroupId,
                name = "Hakket oksekød 12% 500 g",
                size = 500f,
                unitType = "g",
                imagePath = null
            ),
            Item(
                id = 0,
                itemGroupId = onionGroupId,
                name = "Gule løg 1 kg",
                size = 1000f,
                unitType = "g",
                imagePath = null
            ),
            Item(
                id = 0,
                itemGroupId = onionGroupId,
                name = "Røde løg 500 g",
                size = 500f,
                unitType = "g",
                imagePath = null
            ),
            Item(
                id = 0,
                itemGroupId = chickenGroupId,
                name = "Kyllingebryst 400 g",
                size = 400f,
                unitType = "g",
                imagePath = null
            ),
            Item(
                id = 0,
                itemGroupId = pepperGroupId,
                name = "Rød peberfrugt",
                size = 250f,
                unitType = "g",
                imagePath = null
            ),
            Item(
                id = 0,
                itemGroupId = soyGroupId,
                name = "Sojasauce 150 ml",
                size = 150f,
                unitType = "ml",
                imagePath = null
            ),
            Item(
                id = 0,
                itemGroupId = soyGroupId,
                name = "Sojasauce med lavt saltindhold 250 ml",
                size = 250f,
                unitType = "ml",
                imagePath = null
            )
        )


        val itemIds = items.map { itemDao.insert(it).toInt() }

        /* ---------- ITEM WEEKLY PRICES ---------- */

        itemIds.forEachIndexed { index, itemId ->
            itemWeeklyPriceDao.insert(
                ItemWeeklyPrice(itemId, year, week, 10f + index, nettoId)
            )
            itemWeeklyPriceDao.insert(
                ItemWeeklyPrice(itemId, year, week, 11f + index, kvicklyId)
            )
        }

        /* ---------- RECIPES ---------- */

        val spaghettiId = recipeDao.insert(
            Recipe(
                title = "Spaghetti Bolognese",
                preparationTimeMinutes = 45,
                description = "Klassisk italiensk pastaret",
                instructions = "Kog pasta. Brun kødet. Tilsæt sauce.",
                imagePath = copyDrawableToInternalStorage(
                    context,
                    R.drawable.recipe_1,
                    "spaghetti.webp"
                ),
                deletable = false
            )
        ).toInt()

        val wokId = recipeDao.insert(
            Recipe(
                title = "Kylling wokret",
                preparationTimeMinutes = 30,
                description = "Hurtig asiatisk wokret",
                instructions = "Steg kylling. Tilsæt grøntsager. Tilsæt sojasauce.",
                imagePath = copyDrawableToInternalStorage(
                    context,
                    R.drawable.recipe_2,
                    "wok.webp"
                ),
                deletable = false
            )
        ).toInt()

        /* ---------- RECIPE ITEMS ---------- */

        recipeItemDao.insertAll(
            listOf(
                RecipeItem(spaghettiId, pastaGroupId, 200f),
                RecipeItem(spaghettiId, beefGroupId, 300f),
                RecipeItem(spaghettiId, onionGroupId, 100f),

                RecipeItem(wokId, chickenGroupId, 300f),
                RecipeItem(wokId, pepperGroupId, 150f),
                RecipeItem(wokId, onionGroupId, 100f)
            )
        )

        /* ---------- SHOPPING LIST ---------- */

        val shoppingListId = shoppingListDao.insert(
            ShoppingList(name = "Weekly groceries")
        ).toInt()

        /* ---------- SHOPPING LIST ITEMS ---------- */

        shoppingListItemDao.insert(
            ShoppingListItem(
                shoppingListId = shoppingListId,
                itemId = itemIds[0],
                calcQuantity = 1,
                isChecked = false
            )
        )

        shoppingListItemDao.insert(
            ShoppingListItem(
                shoppingListId = shoppingListId,
                itemId = itemIds[2],
                calcQuantity = 2,
                isChecked = false
            )
        )

        shoppingListItemDao.insert(
            ShoppingListItem(
                shoppingListId = shoppingListId,
                itemId = itemIds[4],
                calcQuantity = 1,
                isChecked = true
            )
        )
    }
}
