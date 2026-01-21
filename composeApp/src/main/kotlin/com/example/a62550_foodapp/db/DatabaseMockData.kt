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


        // IDEMPOTENT GUARD
        if (database.itemGroupDao().count() > 0) return@withContext

        val itemGroupDao = database.itemGroupDao()
        val itemDao = database.itemDao()
        val supermarketDao = database.supermarketDao()
        val itemWeeklyPriceDao = database.itemWeeklyPriceDao()
        val recipeDao = database.recipeDao()
        val recipeItemDao = database.recipeItemDao()
        val shoppingListDao = database.shoppingListDao()
        val shoppingListItemGroupDao = database.shoppingListItemGroupDao()

        val year = 2025
        val week = 1


        // SUPERMARKETS
        val nettoId = supermarketDao.insert(Supermarket(name = "Netto", logo = null))
        val kvicklyId = supermarketDao.insert(Supermarket(name = "Kvickly", logo = null))
        val foetexId = supermarketDao.insert(Supermarket(name = "Føtex", logo = null))
        val menyId = supermarketDao.insert(Supermarket(name = "Meny", logo = null))
        val bilkaId = supermarketDao.insert(Supermarket(name = "Bilka", logo = null))
        val rema1000Id = supermarketDao.insert(Supermarket(name = "Rema 1000", logo = null))

        val supermarkets = listOf(
            nettoId, kvicklyId, foetexId, menyId, bilkaId, rema1000Id
        )

        // ITEM GROUPS (semantic ingredients)
        suspend fun group(name: String, category: String, unit: String): Long =
            itemGroupDao.insert(
                ItemGroup(
                    name = name,
                    category = category,
                    unitType = unit
                )
            )

        val pasta = group("Pasta", "Tørvarer", "g")
        val rice = group("Ris", "Tørvarer", "g")
        val beef = group("Oksekød", "Kød", "g")
        val chicken = group("Kylling", "Kød", "g")
        val onion = group("Løg", "Grøntsager", "g")
        val tomato = group("Tomat", "Grøntsager", "g")
        val milk = group("Mælk", "Mejeri", "ml")
        val cheese = group("Ost", "Mejeri", "g")
        val eggs = group("Æg", "Mejeri", "stk")
        val fish = group("Laks", "Fisk", "g")

        val fallbackGroup = group("Mystisk ingrediens", "Ukendt", "g")

        // ITEMS (concrete products)
        fun item(
            groupId: Long,
            name: String,
            size: Float,
            unit: String
        ): Item =
            Item(
                itemGroupId = groupId,
                name = name,
                size = size,
                unitType = unit,
                imagePath = null
            )

        val items = listOf(
            item(pasta, "Spaghetti 500g", 500f, "g"),
            item(pasta, "Penne 1kg", 1000f, "g"),
            item(rice, "Basmati ris 1kg", 1000f, "g"),
            item(beef, "Hakket oksekød 400g", 400f, "g"),
            item(chicken, "Kyllingebryst 400g", 400f, "g"),
            item(onion, "Gule løg 1kg", 1000f, "g"),
            item(tomato, "Tomater 500g", 500f, "g"),
            item(milk, "Letmælk 1L", 1000f, "ml"),
            item(cheese, "Revet ost 200g", 200f, "g"),
            item(eggs, "Æg 10 stk", 10f, "stk"),
            item(fish, "Laks 300g", 300f, "g")
        )

        val itemIds = items.map { itemDao.insert(it) }

        // WEEKLY PRICES (per item per supermarket)
        itemIds.forEachIndexed { index, itemId ->
            supermarkets.forEachIndexed { storeIndex, storeId ->
                itemWeeklyPriceDao.insert(
                    ItemWeeklyPrice(
                        item_id = itemId,
                        supermarket_id = storeId,
                        year = year,
                        week = week,
                        price = 9f + ((index + storeIndex) % 6)
                    )
                )
            }
        }

        // ---------------------------------------------------------------------
        // RECIPES
        // ---------------------------------------------------------------------

        suspend fun recipe(
            title: String,
            minutes: Int,
            desc: String,
            image: Int?
        ): Long =
            recipeDao.insert(
                Recipe(
                    title = title,
                    preparationTimeMinutes = minutes,
                    description = desc,
                    instructions = "Tilbered efter smag.",
                    imagePath = image?.let {
                        copyDrawableToInternalStorage(context, it, "$title.webp")
                    },
                    deletable = false
                )
            )

        val spaghetti = recipe(
            "Spaghetti Bolognese",
            45,
            "Italiensk klassiker",
            R.drawable.recipe_1
        )

        val chickenWok = recipe(
            "Chicken Wok",
            30,
            "Asiatisk wok",
            R.drawable.recipe_2
        )

        val fallbackRecipe = recipe(
            "Fallback Opskrift",
            10,
            "Tester fallback uden rigtige items",
            null
        )

        // RECIPE ITEMS (recipe → item groups)
        recipeItemDao.insertAll(
            listOf(
                RecipeItem(spaghetti, pasta, 200),
                RecipeItem(spaghetti, beef, 300),
                RecipeItem(spaghetti, tomato, 200),

                RecipeItem(chickenWok, chicken, 300),
                RecipeItem(chickenWok, onion, 150),

                RecipeItem(fallbackRecipe, fallbackGroup, 100)
            )
        )

        // SHOPPING LIST (item groups only)
        val shoppingListId =
            shoppingListDao.insert(ShoppingList(name = "Weekly groceries"))

        shoppingListItemGroupDao.insert(
            ShoppingListItemGroup(
                shoppingListId = shoppingListId,
                itemGroupId = pasta,
                recipeId = spaghetti,
                portionQuantity = 2,
                portionSize = 200f,
                isChecked = false
            )
        )

        shoppingListItemGroupDao.insert(
            ShoppingListItemGroup(
                shoppingListId = shoppingListId,
                itemGroupId = fallbackGroup,
                recipeId = null,
                portionQuantity = 1,
                portionSize = 100f,
                isChecked = false
            )
        )
    }
}
