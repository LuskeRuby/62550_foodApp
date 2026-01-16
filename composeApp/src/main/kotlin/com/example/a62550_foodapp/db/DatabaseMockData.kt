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

        val nettoId = supermarketDao.insert(Supermarket(name = "Netto", logo = null)).toInt()
        val kvicklyId = supermarketDao.insert(Supermarket(name = "Kvickly", logo = null)).toInt()

        /* ---------- ITEM GROUPS ---------- */

        suspend fun g(name: String, cat: String, unit: String): Int =
            itemGroupDao.insert(ItemGroup(name = name, category = cat, unitType = unit)).toInt()

        val pasta = g("Pasta", "Tørvarer", "g")
        val rice = g("Ris", "Tørvarer", "g")
        val beef = g("Oksekød", "Kød", "g")
        val chicken = g("Kylling", "Kød", "g")
        val pork = g("Svinekød", "Kød", "g")
        val onion = g("Løg", "Grøntsager", "g")
        val garlic = g("Hvidløg", "Grøntsager", "g")
        val carrot = g("Gulerod", "Grøntsager", "g")
        val pepper = g("Peberfrugt", "Grøntsager", "g")
        val tomato = g("Tomat", "Grøntsager", "g")
        val broccoli = g("Broccoli", "Grøntsager", "g")
        val cream = g("Fløde", "Mejeri", "ml")
        val cheese = g("Ost", "Mejeri", "g")
        val milk = g("Mælk", "Mejeri", "ml")
        val curry = g("Karry", "Krydderier", "g")
        val chili = g("Chili", "Krydderier", "g")
        val soy = g("Sojasauce", "Kolonial", "ml")
        val beans = g("Bønner", "Kolonial", "g")
        val corn = g("Majs", "Kolonial", "g")
        val wraps = g("Wraps", "Brød", "stk")
        val bread = g("Brød", "Brød", "stk")
        val eggs = g("Æg", "Mejeri", "stk")
        val butter = g("Smør", "Mejeri", "g")
        val mushrooms = g("Champignon", "Grøntsager", "g")
        val fish = g("Laks", "Fisk", "g")

        /* ---------- ITEMS ---------- */

        fun item(g: Int, name: String, size: Float, unit: String) =
            Item(id = 0, itemGroupId = g, name = name, size = size, unitType = unit, imagePath = null)

        val items = listOf(
            item(pasta, "Spaghetti 500g", 500f, "g"),
            item(pasta, "Penne 1kg", 1000f, "g"),
            item(rice, "Basmati ris 1kg", 1000f, "g"),
            item(rice, "Jasmin ris 1kg", 1000f, "g"),
            item(beef, "Hakket oksekød 400g", 400f, "g"),
            item(beef, "Oksekød i tern 500g", 500f, "g"),
            item(chicken, "Kyllingebryst 400g", 400f, "g"),
            item(chicken, "Kyllingelår 600g", 600f, "g"),
            item(pork, "Svinefilet 500g", 500f, "g"),
            item(onion, "Gule løg 1kg", 1000f, "g"),
            item(garlic, "Hvidløg 100g", 100f, "g"),
            item(carrot, "Gulerødder 1kg", 1000f, "g"),
            item(pepper, "Rød peber 300g", 300f, "g"),
            item(tomato, "Tomater 500g", 500f, "g"),
            item(broccoli, "Broccoli 400g", 400f, "g"),
            item(cream, "Madlavningsfløde 250ml", 250f, "ml"),
            item(cheese, "Revet ost 200g", 200f, "g"),
            item(milk, "Letmælk 1L", 1000f, "ml"),
            item(curry, "Karry pulver 50g", 50f, "g"),
            item(chili, "Chiliflager 30g", 30f, "g"),
            item(soy, "Sojasauce 250ml", 250f, "ml"),
            item(beans, "Kidneybønner 400g", 400f, "g"),
            item(corn, "Majs 300g", 300f, "g"),
            item(wraps, "Tortilla wraps 8 stk", 8f, "stk"),
            item(bread, "Rugbrød", 1f, "stk"),
            item(eggs, "Æg 10 stk", 10f, "stk"),
            item(butter, "Smør 200g", 200f, "g"),
            item(mushrooms, "Champignon 250g", 250f, "g"),
            item(fish, "Laks 300g", 300f, "g")
        )

        val itemIds = items.map { itemDao.insert(it).toInt() }

        /* ---------- PRICES ---------- */
        itemIds.forEachIndexed { index, itemId ->
            itemWeeklyPriceDao.insert(ItemWeeklyPrice(itemId, year, week, 10f + index % 7, nettoId))
            itemWeeklyPriceDao.insert(ItemWeeklyPrice(itemId, year, week, 11f + index % 7, kvicklyId))
        }

        /* ---------- RECIPES ---------- */

        suspend fun recipe(
            title: String,
            time: Int,
            desc: String,
            img: Int?
        ): Int = recipeDao.insert(
            Recipe(
                title = title,
                preparationTimeMinutes = time,
                description = desc,
                instructions = "Tilbered efter smag.",
                imagePath = img?.let { copyDrawableToInternalStorage(context, it, "$title.webp") },
                deletable = false
            )
        ).toInt()

        val r1 = recipe("Spaghetti Bolognese", 45, "Italiensk klassiker", R.drawable.recipe_1)
        val r2 = recipe("Chicken Wok", 30, "Asiatisk wok", R.drawable.recipe_2)
        val r3 = recipe("Chili con Carne", 50, "Krydret gryderet", R.drawable.recipe_3)
        val r4 = recipe("Pasta Alfredo", 25, "Cremet pasta", R.drawable.recipe_4)
        val r5 = recipe("Chicken Curry", 40, "Karryret", R.drawable.recipe_5)
        val r6 = recipe("Wraps med kylling", 20, "Let aftensmad", R.drawable.recipe_6)
        val r7 = recipe("Veggie Wok", 25, "Grøntsagswok", R.drawable.recipe_7)
        val r8 = recipe("Ris med oksekød", 35, "Hurtig hverdagsret", R.drawable.recipe_8)
        val r9 = recipe("Tomatsuppe", 30, "Varm suppe", R.drawable.recipe_9)
        val r10 = recipe("Laks med grønt", 35, "Sund fiskeret", R.drawable.recipe_10)

        /* ---------- RECIPE ITEMS ---------- */

        recipeItemDao.insertAll(
            listOf(
                RecipeItem(r1, pasta, 200), RecipeItem(r1, beef, 300), RecipeItem(r1, tomato, 200),
                RecipeItem(r2, chicken, 300), RecipeItem(r2, pepper, 150), RecipeItem(r2, soy, 30),
                RecipeItem(r3, beef, 300), RecipeItem(r3, beans, 200), RecipeItem(r3, chili, 5),
                RecipeItem(r4, pasta, 200), RecipeItem(r4, cream, 150), RecipeItem(r4, cheese, 80),
                RecipeItem(r5, chicken, 300), RecipeItem(r5, curry, 10), RecipeItem(r5, rice, 200),
                RecipeItem(r6, wraps, 4), RecipeItem(r6, chicken, 200), RecipeItem(r6, corn, 100),
                RecipeItem(r7, broccoli, 200), RecipeItem(r7, carrot, 150), RecipeItem(r7, soy, 20),
                RecipeItem(r8, rice, 200), RecipeItem(r8, beef, 250), RecipeItem(r8, onion, 100),
                RecipeItem(r9, tomato, 300), RecipeItem(r9, cream, 100), RecipeItem(r9, garlic, 10),
                RecipeItem(r10, fish, 300), RecipeItem(r10, broccoli, 200), RecipeItem(r10, carrot, 100),
            )
        )

        /* ---------- SHOPPING LIST ---------- */

        val shoppingListId = shoppingListDao.insert(ShoppingList(name = "Weekly groceries")).toInt()

        shoppingListItemDao.insert(ShoppingListItem(shoppingListId, itemIds[0], 1, false))
        shoppingListItemDao.insert(ShoppingListItem(shoppingListId, itemIds[4], 1, false))
        shoppingListItemDao.insert(ShoppingListItem(shoppingListId, itemIds[14], 1, true))
    }
}
