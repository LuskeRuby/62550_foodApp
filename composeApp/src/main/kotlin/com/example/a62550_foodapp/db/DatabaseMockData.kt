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
        val shoppingListItemGroupDao = database.shoppingListItemGroupDao()

        val year = 2025
        val week = 1

        /* ---------- SUPERMARKETS ---------- */

        val nettoId = supermarketDao.insert(Supermarket(name = "Netto", logo = null)).toInt()
        val kvicklyId = supermarketDao.insert(Supermarket(name = "Kvickly", logo = null)).toInt()
        val foetexId = supermarketDao.insert(Supermarket(name = "Føtex", logo = null)).toInt()
        val menyId = supermarketDao.insert(Supermarket(name = "Meny", logo = null)).toInt()
        val bilkaId = supermarketDao.insert(Supermarket(name = "Bilka", logo = null)).toInt()
        val rema1000Id = supermarketDao.insert(Supermarket(name = "Rema 1000", logo = null)).toInt()

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
        val bacon = g("Bacon", "Kød", "g")
        val potatoes = g("Kartofler", "Grøntsager", "g")
        val peas = g("Ærter", "Kolonial", "g")
        val groundMeat = g("Hakket kød", "Kød", "g")
        val leek = g("Purløg", "Grøntsager", "g")
        val beetroot = g("Rødbede", "Grøntsager", "g")
        val redCabbage = g("Rødkål", "Grøntsager", "g")
        val ham = g("Skinke", "Kød", "g")
        val herring = g("Sild", "Fisk", "g")
        val appleSyrup = g("Æbleskivemasse", "Kolonial", "g")
        val flour = g("Mel", "Kolonial", "g")
        val pork_flank = g("Stegt flæsk", "Kød", "g")
        val fallbackGroup = g("Mystisk ingrediens", "Ukendt", "g")

        /* ---------- ITEMS ---------- */

        fun item(g: Int, name: String, size: Float, unit: String) =
            Item(itemGroupId = g.toLong(), name = name, size = size, unitType = unit, imagePath = null)

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
            item(fish, "Laks 300g", 300f, "g"),
            item(bacon, "Bacon i skiver 200g", 200f, "g"),
            item(potatoes, "Kartofler 2kg", 2000f, "g"),
            item(peas, "Ærter 400g", 400f, "g"),
            item(groundMeat, "Hakket svinekød 400g", 400f, "g"),
            item(pork, "Svinemorbrád 800g", 800f, "g"),
            item(leek, "Purløg 500g", 500f, "g"),
            item(beetroot, "Rødbeder 500g", 500f, "g"),
            item(redCabbage, "Rødkål 1kg", 1000f, "g"),
            item(ham, "Rugbrødsskinke 200g", 200f, "g"),
            item(herring, "Sildefilet 150g", 150f, "g"),
            item(appleSyrup, "Æbleskivemasse 200g", 200f, "g"),
            item(flour, "Hvedemel 1kg", 1000f, "g"),
            item(pork_flank, "Stegt flæsk 200g", 200f, "g"),
            item(tomato, "Tomatskiver i glas 400g", 400f, "g")
        )

        val itemIds = items.map { itemDao.insert(it).toInt() }

        /* ---------- PRICES ---------- */

        itemIds.forEachIndexed { index, itemId ->
            itemWeeklyPriceDao.insert(ItemWeeklyPrice(itemId.toLong(), nettoId.toLong(), year, week, 10f + index % 7))
            itemWeeklyPriceDao.insert(ItemWeeklyPrice(itemId.toLong(), kvicklyId.toLong(), year, week, 11f + index % 7))
            itemWeeklyPriceDao.insert(ItemWeeklyPrice(itemId.toLong(), foetexId.toLong(), year, week, 9.5f + index % 7))
            itemWeeklyPriceDao.insert(ItemWeeklyPrice(itemId.toLong(), menyId.toLong(), year, week, 12.5f + index % 7))
            itemWeeklyPriceDao.insert(ItemWeeklyPrice(itemId.toLong(), bilkaId.toLong(), year, week, 9f + index % 7))
            itemWeeklyPriceDao.insert(ItemWeeklyPrice(itemId.toLong(), rema1000Id.toLong(), year, week, 10.5f + index % 7))
        }

        /* ---------- RECIPES ---------- */

        suspend fun recipe(title: String, time: Int, desc: String, img: Int?): Int =
            recipeDao.insert(
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
        val r11 = recipe("Mørbrad gryde", 40, "Lækkert stegt mel fra Sverige", R.drawable.recipe_11)
        val r12 = recipe("Frikadeller", 40, "Danske klassiker med rødkål", R.drawable.recipe_12)
        val r13 = recipe("Æbleskiver", 35, "Søde danske æbleskiver", R.drawable.recipe_13)
        val r14 = recipe("Stegt flæsk og løg", 45, "Dansk klassiker med kartofler", R.drawable.recipe_14)
        val r15 = recipe("Rugbrødsmørrebrød", 20, "Let frokost med skinke", R.drawable.recipe_15)
        val r16 = recipe("Rødkål gryde", 50, "Varm og behagelig ret", R.drawable.recipe_16)
        val r17 = recipe("Medister med kartofler", 35, "Klassisk dansk medisterpølse", R.drawable.recipe_17)
        val r18 = recipe("Karbonader", 40, "Stegt kød med sauce", R.drawable.recipe_18)
        val r19 = recipe("Pølser med kartofler", 30, "Dansk husmannskost", R.drawable.recipe_19)
        val r20 = recipe("Kylling i flødesauce", 45, "Cremet og lækker", R.drawable.recipe_20)
        val fallbackRecipe = recipe("Fallback Opskrift", 10, "Tester opskrift fallback uden konkrete items", null)

        /* ---------- RECIPE ITEMS ---------- */

        recipeItemDao.insertAll(
            listOf(
                RecipeItem(r1.toLong(), pasta.toLong(), 200),
                RecipeItem(r1.toLong(), beef.toLong(), 300),
                RecipeItem(r1.toLong(), tomato.toLong(), 200),
                RecipeItem(r2.toLong(), chicken.toLong(), 300),
                RecipeItem(r2.toLong(), pepper.toLong(), 150),
                RecipeItem(r2.toLong(), soy.toLong(), 30),
                RecipeItem(r3.toLong(), beef.toLong(), 300),
                RecipeItem(r3.toLong(), beans.toLong(), 200),
                RecipeItem(r3.toLong(), chili.toLong(), 5),
                RecipeItem(fallbackRecipe.toLong(), fallbackGroup.toLong(), 150),
                RecipeItem(fallbackRecipe.toLong(), fallbackGroup.toLong(), 75)
            )
        )

        /* ---------- SHOPPING LIST ---------- */

        val shoppingListId = shoppingListDao.insert(ShoppingList(name = "Weekly groceries")).toInt()


    }
}
