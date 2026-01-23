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

        val nettoId = supermarketDao.insert(Supermarket(name = "Netto", logo = copyDrawableToInternalStorage(context, R.drawable.netto, "netto.png"))).toInt()
        val kvicklyId = supermarketDao.insert(Supermarket(name = "Kvickly", logo = copyDrawableToInternalStorage(context, R.drawable.kvickly, "kvickly.png"))).toInt()
        val foetexId = supermarketDao.insert(Supermarket(name = "Føtex", logo = copyDrawableToInternalStorage(context, R.drawable.foetex, "foetex.png"))).toInt()
        val menyId = supermarketDao.insert(Supermarket(name = "Meny", logo = copyDrawableToInternalStorage(context, R.drawable.meny, "meny.png"))).toInt()
        val bilkaId = supermarketDao.insert(Supermarket(name = "Bilka", logo = copyDrawableToInternalStorage(context, R.drawable.bilka, "bilka.png"))).toInt()
        val rema1000Id = supermarketDao.insert(Supermarket(name = "Rema 1000", logo = copyDrawableToInternalStorage(context, R.drawable.rema, "rema.png"))).toInt()

        /* ---------- ITEM GROUPS ---------- */

        suspend fun g(name: String, cat: String, unit: String): Int =
            itemGroupDao.insert(ItemGroup(name = name, category = cat, unitType = unit)).toInt()

        val pasta = g("Pasta", "Tørvarer", "g")
        val rice = g("Ris", "Tørvarer", "g")

        val beef = g("Oksekød", "Kød", "g")
        val chicken = g("Kylling", "Kød", "g")
        val pork = g("Svinekød", "Kød", "g")
        val bacon = g("Bacon", "Kød", "g")
        val groundMeat = g("Hakket kød", "Kød", "g")
        val ham = g("Skinke", "Kød", "g")
        val porkFlank = g("Stegt flæsk", "Kød", "g")

        val fish = g("Laks", "Fisk", "g")
        val herring = g("Sild", "Fisk", "g")

        val onion = g("Løg", "Grøntsager", "stk")
        val garlic = g("Hvidløg", "Grøntsager", "g")
        val carrot = g("Gulerod", "Grøntsager", "g")
        val pepper = g("Peberfrugt", "Grøntsager", "g")
        val tomato = g("Tomat", "Grøntsager", "g")
        val broccoli = g("Broccoli", "Grøntsager", "g")
        val mushrooms = g("Champignon", "Grøntsager", "g")
        val potatoes = g("Kartofler", "Grøntsager", "g")
        val peas = g("Ærter", "Kolonial", "g")
        val chives = g("Purløg", "Grøntsager", "g")
        val beetroot = g("Rødbede", "Grøntsager", "g")
        val redCabbage = g("Rødkål", "Grøntsager", "g")

        val cream = g("Fløde", "Mejeri", "ml")
        val milk = g("Mælk", "Mejeri", "ml")
        val cheese = g("Ost", "Mejeri", "g")
        val butter = g("Smør", "Mejeri", "g")
        val eggs = g("Æg", "Mejeri", "stk")

        val curry = g("Karry", "Krydderier", "g")
        val chili = g("Chili", "Krydderier", "g")

        val soy = g("Sojasauce", "Kolonial", "ml")
        val beans = g("Bønner", "Kolonial", "g")
        val corn = g("Majs", "Kolonial", "g")
        val appleFilling = g("Æbleskivemasse", "Kolonial", "g")
        val flour = g("Mel", "Kolonial", "g")

        val wraps = g("Wraps", "Brød", "stk")
        val bread = g("Brød", "Brød", "stk")



        /* ---------- ITEMS ---------- */

        fun item(g: Int, name: String, size: Float, unit: String) =
            Item(itemGroupId = g.toLong(), name = name, size = size, unitType = unit, imagePath = null)

        val items = listOf(
            item(pasta, "Spaghetti 500 g", 500f, "g"),
            item(pasta, "Penne 1 kg", 1000f, "g"),

            item(rice, "Basmati ris 1 kg", 1000f, "g"),
            item(rice, "Jasmin ris 1 kg", 1000f, "g"),

            item(beef, "Hakket oksekød 400 g", 400f, "g"),
            item(beef, "Oksekød i tern 500 g", 500f, "g"),

            item(chicken, "Kyllingebryst 400 g", 400f, "g"),
            item(chicken, "Kyllingelår 600 g", 600f, "g"),

            item(pork, "Svinefilet 500 g", 500f, "g"),
            item(pork, "Svinemørbrad 800 g", 800f, "g"),

            item(onion, "Gule løg 1 kg", 1000f, "g"),
            item(garlic, "Hvidløg 100 g", 100f, "g"),
            item(carrot, "Gulerødder 1 kg", 1000f, "g"),
            item(pepper, "Rød peberfrugt 300 g", 300f, "g"),
            item(tomato, "Tomater 500 g", 500f, "g"),
            item(tomato, "Tomatskiver i glas 400 g", 400f, "g"),
            item(broccoli, "Broccoli 400 g", 400f, "g"),

            item(cream, "Madlavningsfløde 250 ml", 250f, "ml"),
            item(milk, "Letmælk 1 L", 1000f, "ml"),
            item(cheese, "Revet ost 200 g", 200f, "g"),
            item(butter, "Smør 200 g", 200f, "g"),
            item(eggs, "Æg 10 stk", 10f, "stk"),

            item(curry, "Karrypulver 50 g", 50f, "g"),
            item(chili, "Chiliflager 30 g", 30f, "g"),

            item(soy, "Sojasauce 250 ml", 250f, "ml"),
            item(beans, "Kidneybønner 400 g", 400f, "g"),
            item(corn, "Majs 300 g", 300f, "g"),
            item(peas, "Ærter 400 g", 400f, "g"),

            item(wraps, "Tortilla wraps 8 stk", 8f, "stk"),
            item(bread, "Rugbrød", 1f, "stk"),

            item(mushrooms, "Champignon 250 g", 250f, "g"),
            item(potatoes, "Kartofler 2 kg", 2000f, "g"),

            item(fish, "Laks 300 g", 300f, "g"),
            item(herring, "Sildefilet 150 g", 150f, "g"),

            item(bacon, "Bacon i skiver 200 g", 200f, "g"),
            item(groundMeat, "Hakket svinekød 400 g", 400f, "g"),
            item(ham, "Rugbrødsskinke 200 g", 200f, "g"),
            item(porkFlank, "Stegt flæsk 200 g", 200f, "g"),

            item(chives, "Purløg 50 g", 50f, "g"),
            item(beetroot, "Rødbeder 500 g", 500f, "g"),
            item(redCabbage, "Rødkål 1 kg", 1000f, "g"),

            item(appleFilling, "Æbleskivemasse 200 g", 200f, "g"),
            item(flour, "Hvedemel 1 kg", 1000f, "g"),

        )

        val itemIds = items.map { itemDao.insert(it).toInt() }

        /* ---------- PRICES (PER ITEM, PER SUPERMARKET — EXPLICIT) ---------- */
        // Prices are varied so different supermarkets are cheapest for different items
        // This ensures multiple supermarket banners appear in the shopping list

        fun price(itemId: Int, marketId: Int, p: Float) {
            itemWeeklyPriceDao.insert(
                ItemWeeklyPrice(itemId.toLong(), marketId.toLong(), year, week, p)
            )
        }

        var i = 0

// ===== PASTA (Netto is cheapest) =====
        val spaghetti = itemIds[i++]
        price(spaghetti, nettoId, 8.95f)      // CHEAPEST
        price(spaghetti, kvicklyId, 11.95f)
        price(spaghetti, foetexId, 10.50f)
        price(spaghetti, menyId, 13.95f)
        price(spaghetti, bilkaId, 9.50f)
        price(spaghetti, rema1000Id, 10.00f)

        val penne = itemIds[i++]
        price(penne, nettoId, 11.95f)         // CHEAPEST
        price(penne, kvicklyId, 14.95f)
        price(penne, foetexId, 13.50f)
        price(penne, menyId, 16.95f)
        price(penne, bilkaId, 12.50f)
        price(penne, rema1000Id, 13.00f)

// ===== RICE (Føtex is cheapest) =====
        val basmati = itemIds[i++]
        price(basmati, nettoId, 14.95f)
        price(basmati, kvicklyId, 14.50f)
        price(basmati, foetexId, 11.00f)      // CHEAPEST
        price(basmati, menyId, 16.00f)
        price(basmati, bilkaId, 12.50f)
        price(basmati, rema1000Id, 13.50f)

        val jasmin = itemIds[i++]
        price(jasmin, nettoId, 15.95f)
        price(jasmin, kvicklyId, 15.50f)
        price(jasmin, foetexId, 12.00f)       // CHEAPEST
        price(jasmin, menyId, 17.00f)
        price(jasmin, bilkaId, 13.50f)
        price(jasmin, rema1000Id, 14.50f)

// ===== BEEF (Rema 1000 is cheapest) =====
        val beefMinced = itemIds[i++]
        price(beefMinced, nettoId, 42f)
        price(beefMinced, kvicklyId, 44f)
        price(beefMinced, foetexId, 43f)
        price(beefMinced, menyId, 48f)
        price(beefMinced, bilkaId, 40f)
        price(beefMinced, rema1000Id, 35f)    // CHEAPEST

        val beefCubes = itemIds[i++]
        price(beefCubes, nettoId, 45f)
        price(beefCubes, kvicklyId, 47f)
        price(beefCubes, foetexId, 46f)
        price(beefCubes, menyId, 52f)
        price(beefCubes, bilkaId, 43f)
        price(beefCubes, rema1000Id, 38f)     // CHEAPEST

// ===== CHICKEN (Bilka is cheapest) =====
        val chickenBreast = itemIds[i++]
        price(chickenBreast, nettoId, 32f)
        price(chickenBreast, kvicklyId, 35f)
        price(chickenBreast, foetexId, 33f)
        price(chickenBreast, menyId, 38f)
        price(chickenBreast, bilkaId, 26f)    // CHEAPEST
        price(chickenBreast, rema1000Id, 30f)

        val chickenLegs = itemIds[i++]
        price(chickenLegs, nettoId, 28f)
        price(chickenLegs, kvicklyId, 31f)
        price(chickenLegs, foetexId, 29f)
        price(chickenLegs, menyId, 35f)
        price(chickenLegs, bilkaId, 22f)      // CHEAPEST
        price(chickenLegs, rema1000Id, 27f)

// ===== VEGETABLES =====
        // Onion - Kvickly is cheapest
        val onionItem = itemIds[i++]
        price(onionItem, nettoId, 8f)
        price(onionItem, kvicklyId, 4.50f)    // CHEAPEST
        price(onionItem, foetexId, 7f)
        price(onionItem, menyId, 9f)
        price(onionItem, bilkaId, 6f)
        price(onionItem, rema1000Id, 7f)

        // Garlic - Meny is cheapest
        val garlicItem = itemIds[i++]
        price(garlicItem, nettoId, 8f)
        price(garlicItem, kvicklyId, 9f)
        price(garlicItem, foetexId, 8.5f)
        price(garlicItem, menyId, 5f)         // CHEAPEST
        price(garlicItem, bilkaId, 7f)
        price(garlicItem, rema1000Id, 7.5f)

        // Carrot - Netto is cheapest
        val carrotItem = itemIds[i++]
        price(carrotItem, nettoId, 5f)        // CHEAPEST
        price(carrotItem, kvicklyId, 8f)
        price(carrotItem, foetexId, 7f)
        price(carrotItem, menyId, 9f)
        price(carrotItem, bilkaId, 6f)
        price(carrotItem, rema1000Id, 6.5f)

        // Pepper - Bilka is cheapest
        val pepperItem = itemIds[i++]
        price(pepperItem, nettoId, 12f)
        price(pepperItem, kvicklyId, 14f)
        price(pepperItem, foetexId, 13f)
        price(pepperItem, menyId, 16f)
        price(pepperItem, bilkaId, 9f)        // CHEAPEST
        price(pepperItem, rema1000Id, 11f)

        // Tomato 1 - Kvickly is cheapest
        val tomato1 = itemIds[i++]
        price(tomato1, nettoId, 10f)
        price(tomato1, kvicklyId, 6f)         // CHEAPEST
        price(tomato1, foetexId, 9f)
        price(tomato1, menyId, 12f)
        price(tomato1, bilkaId, 8f)
        price(tomato1, rema1000Id, 8.5f)

        // Tomato 2 - Føtex is cheapest
        val tomato2 = itemIds[i++]
        price(tomato2, nettoId, 11f)
        price(tomato2, kvicklyId, 10f)
        price(tomato2, foetexId, 7f)          // CHEAPEST
        price(tomato2, menyId, 13f)
        price(tomato2, bilkaId, 9f)
        price(tomato2, rema1000Id, 9.5f)

        // Broccoli - Rema 1000 is cheapest
        val broccoliItem = itemIds[i++]
        price(broccoliItem, nettoId, 12f)
        price(broccoliItem, kvicklyId, 14f)
        price(broccoliItem, foetexId, 13f)
        price(broccoliItem, menyId, 16f)
        price(broccoliItem, bilkaId, 11f)
        price(broccoliItem, rema1000Id, 8f)   // CHEAPEST

// ===== MEJERI =====
        // Cream - Meny is cheapest
        val creamItem = itemIds[i++]
        price(creamItem, nettoId, 10f)
        price(creamItem, kvicklyId, 11f)
        price(creamItem, foetexId, 10.5f)
        price(creamItem, menyId, 7f)          // CHEAPEST
        price(creamItem, bilkaId, 9f)
        price(creamItem, rema1000Id, 9.5f)

        // Milk - Kvickly is cheapest
        val milkItem = itemIds[i++]
        price(milkItem, nettoId, 9f)
        price(milkItem, kvicklyId, 6f)        // CHEAPEST
        price(milkItem, foetexId, 8f)
        price(milkItem, menyId, 10f)
        price(milkItem, bilkaId, 7.5f)
        price(milkItem, rema1000Id, 8f)

        // Cheese - Netto is cheapest
        val cheeseItem = itemIds[i++]
        price(cheeseItem, nettoId, 15f)       // CHEAPEST
        price(cheeseItem, kvicklyId, 20f)
        price(cheeseItem, foetexId, 18f)
        price(cheeseItem, menyId, 22f)
        price(cheeseItem, bilkaId, 17f)
        price(cheeseItem, rema1000Id, 18f)

        // Butter - Bilka is cheapest
        val butterItem = itemIds[i++]
        price(butterItem, nettoId, 14f)
        price(butterItem, kvicklyId, 16f)
        price(butterItem, foetexId, 15f)
        price(butterItem, menyId, 18f)
        price(butterItem, bilkaId, 10f)       // CHEAPEST
        price(butterItem, rema1000Id, 13f)

        // Eggs - Føtex is cheapest
        val eggsItem = itemIds[i++]
        price(eggsItem, nettoId, 18f)
        price(eggsItem, kvicklyId, 20f)
        price(eggsItem, foetexId, 14f)        // CHEAPEST
        price(eggsItem, menyId, 22f)
        price(eggsItem, bilkaId, 17f)
        price(eggsItem, rema1000Id, 16f)

// ===== KRYDDERIER (Spices) =====
        // Curry - Rema 1000 is cheapest
        val curryItem = itemIds[i++]
        price(curryItem, nettoId, 9f)
        price(curryItem, kvicklyId, 11f)
        price(curryItem, foetexId, 10f)
        price(curryItem, menyId, 13f)
        price(curryItem, bilkaId, 8f)
        price(curryItem, rema1000Id, 6f)      // CHEAPEST

        // Chili - Meny is cheapest
        val chiliItem = itemIds[i++]
        price(chiliItem, nettoId, 10f)
        price(chiliItem, kvicklyId, 12f)
        price(chiliItem, foetexId, 11f)
        price(chiliItem, menyId, 6f)          // CHEAPEST
        price(chiliItem, bilkaId, 9f)
        price(chiliItem, rema1000Id, 8f)

// ===== KOLONIAL =====
        // Soy - Netto is cheapest
        val soyItem = itemIds[i++]
        price(soyItem, nettoId, 7f)           // CHEAPEST
        price(soyItem, kvicklyId, 12f)
        price(soyItem, foetexId, 10f)
        price(soyItem, menyId, 14f)
        price(soyItem, bilkaId, 9f)
        price(soyItem, rema1000Id, 9f)

        // Beans - Kvickly is cheapest
        val beansItem = itemIds[i++]
        price(beansItem, nettoId, 10f)
        price(beansItem, kvicklyId, 6f)       // CHEAPEST
        price(beansItem, foetexId, 9f)
        price(beansItem, menyId, 12f)
        price(beansItem, bilkaId, 8f)
        price(beansItem, rema1000Id, 8.5f)

        // Corn - Føtex is cheapest
        val cornItem = itemIds[i++]
        price(cornItem, nettoId, 9f)
        price(cornItem, kvicklyId, 10f)
        price(cornItem, foetexId, 6f)         // CHEAPEST
        price(cornItem, menyId, 12f)
        price(cornItem, bilkaId, 8f)
        price(cornItem, rema1000Id, 8f)

        // Peas - Bilka is cheapest
        val peasItem = itemIds[i++]
        price(peasItem, nettoId, 9f)
        price(peasItem, kvicklyId, 10f)
        price(peasItem, foetexId, 9f)
        price(peasItem, menyId, 12f)
        price(peasItem, bilkaId, 5f)          // CHEAPEST
        price(peasItem, rema1000Id, 8f)

// ===== BRØD =====
        // Wraps - Rema 1000 is cheapest
        val wrapsItem = itemIds[i++]
        price(wrapsItem, nettoId, 16f)
        price(wrapsItem, kvicklyId, 18f)
        price(wrapsItem, foetexId, 17f)
        price(wrapsItem, menyId, 20f)
        price(wrapsItem, bilkaId, 15f)
        price(wrapsItem, rema1000Id, 12f)     // CHEAPEST

        // Bread - Kvickly is cheapest
        val breadItem = itemIds[i++]
        price(breadItem, nettoId, 14f)
        price(breadItem, kvicklyId, 10f)      // CHEAPEST
        price(breadItem, foetexId, 13f)
        price(breadItem, menyId, 16f)
        price(breadItem, bilkaId, 12f)
        price(breadItem, rema1000Id, 12f)

// ===== REMAINING ITEMS =====
        // Mushrooms - Meny is cheapest
        val mushroomsItem = itemIds[i++]
        price(mushroomsItem, nettoId, 14f)
        price(mushroomsItem, kvicklyId, 15f)
        price(mushroomsItem, foetexId, 13f)
        price(mushroomsItem, menyId, 9f)      // CHEAPEST
        price(mushroomsItem, bilkaId, 12f)
        price(mushroomsItem, rema1000Id, 11f)

        // Potatoes - Kvickly is cheapest
        val potatoesItem = itemIds[i++]
        price(potatoesItem, nettoId, 18f)
        price(potatoesItem, kvicklyId, 12f)   // CHEAPEST
        price(potatoesItem, foetexId, 16f)
        price(potatoesItem, menyId, 20f)
        price(potatoesItem, bilkaId, 15f)
        price(potatoesItem, rema1000Id, 14f)

// ===== FISK (Fish) =====
        // Salmon - Føtex is cheapest
        val fishItem = itemIds[i++]
        price(fishItem, nettoId, 55f)
        price(fishItem, kvicklyId, 58f)
        price(fishItem, foetexId, 45f)        // CHEAPEST
        price(fishItem, menyId, 62f)
        price(fishItem, bilkaId, 52f)
        price(fishItem, rema1000Id, 50f)

        // Herring - Bilka is cheapest
        val herringItem = itemIds[i++]
        price(herringItem, nettoId, 28f)
        price(herringItem, kvicklyId, 30f)
        price(herringItem, foetexId, 27f)
        price(herringItem, menyId, 32f)
        price(herringItem, bilkaId, 22f)      // CHEAPEST
        price(herringItem, rema1000Id, 25f)

// ===== MORE MEAT =====
        // Bacon - Netto is cheapest
        val baconItem = itemIds[i++]
        price(baconItem, nettoId, 18f)        // CHEAPEST
        price(baconItem, kvicklyId, 24f)
        price(baconItem, foetexId, 22f)
        price(baconItem, menyId, 26f)
        price(baconItem, bilkaId, 20f)
        price(baconItem, rema1000Id, 21f)

        // Ground meat - Rema 1000 is cheapest
        val groundMeatItem = itemIds[i++]
        price(groundMeatItem, nettoId, 35f)
        price(groundMeatItem, kvicklyId, 38f)
        price(groundMeatItem, foetexId, 36f)
        price(groundMeatItem, menyId, 42f)
        price(groundMeatItem, bilkaId, 33f)
        price(groundMeatItem, rema1000Id, 28f) // CHEAPEST

        // Ham - Kvickly is cheapest
        val hamItem = itemIds[i++]
        price(hamItem, nettoId, 22f)
        price(hamItem, kvicklyId, 16f)        // CHEAPEST
        price(hamItem, foetexId, 20f)
        price(hamItem, menyId, 24f)
        price(hamItem, bilkaId, 19f)
        price(hamItem, rema1000Id, 18f)

        // Pork Flank - Meny is cheapest
        val porkFlankItem = itemIds[i++]
        price(porkFlankItem, nettoId, 38f)
        price(porkFlankItem, kvicklyId, 40f)
        price(porkFlankItem, foetexId, 37f)
        price(porkFlankItem, menyId, 30f)     // CHEAPEST
        price(porkFlankItem, bilkaId, 35f)
        price(porkFlankItem, rema1000Id, 34f)

// ===== MORE VEGETABLES =====
        // Chives - Føtex is cheapest
        val chivesItem = itemIds[i++]
        price(chivesItem, nettoId, 10f)
        price(chivesItem, kvicklyId, 12f)
        price(chivesItem, foetexId, 7f)       // CHEAPEST
        price(chivesItem, menyId, 14f)
        price(chivesItem, bilkaId, 9f)
        price(chivesItem, rema1000Id, 8f)

        // Beetroot - Bilka is cheapest
        val beetrootItem = itemIds[i++]
        price(beetrootItem, nettoId, 12f)
        price(beetrootItem, kvicklyId, 14f)
        price(beetrootItem, foetexId, 13f)
        price(beetrootItem, menyId, 16f)
        price(beetrootItem, bilkaId, 8f)      // CHEAPEST
        price(beetrootItem, rema1000Id, 10f)

        // Red Cabbage - Netto is cheapest
        val redCabbageItem = itemIds[i++]
        price(redCabbageItem, nettoId, 10f)   // CHEAPEST
        price(redCabbageItem, kvicklyId, 15f)
        price(redCabbageItem, foetexId, 14f)
        price(redCabbageItem, menyId, 18f)
        price(redCabbageItem, bilkaId, 12f)
        price(redCabbageItem, rema1000Id, 11f)

// ===== MORE KOLONIAL =====
        // Apple Filling - Rema 1000 is cheapest
        val appleFillingItem = itemIds[i++]
        price(appleFillingItem, nettoId, 18f)
        price(appleFillingItem, kvicklyId, 20f)
        price(appleFillingItem, foetexId, 19f)
        price(appleFillingItem, menyId, 24f)
        price(appleFillingItem, bilkaId, 17f)
        price(appleFillingItem, rema1000Id, 14f) // CHEAPEST

        // Flour - Meny is cheapest
        val flourItem = itemIds[i++]
        price(flourItem, nettoId, 12f)
        price(flourItem, kvicklyId, 14f)
        price(flourItem, foetexId, 13f)
        price(flourItem, menyId, 8f)          // CHEAPEST
        price(flourItem, bilkaId, 11f)
        price(flourItem, rema1000Id, 10f)

// ===== Fallback =====
        val fallbackItem = itemIds.last()
        price(fallbackItem, nettoId, 0f)
        price(fallbackItem, kvicklyId, 0f)
        price(fallbackItem, foetexId, 0f)
        price(fallbackItem, menyId, 0f)
        price(fallbackItem, bilkaId, 0f)
        price(fallbackItem, rema1000Id, 0f)



        /* ---------- RECIPES ---------- */

        suspend fun recipe(title: String, time: Int, desc: String, instruct: String?, img: Int?): Int =
            recipeDao.insert(
                Recipe(
                    title = title,
                    preparationTimeMinutes = time,
                    description = desc,
                    instructions = instruct,
                    imagePath = img?.let { copyDrawableToInternalStorage(context, it, "$title.webp") },
                    deletable = false
                )
            ).toInt()

        val r1 = recipe(
            "Spaghetti Bolognese", 45, "Klassisk italiensk kødsauce med dyb smag",
            "Hak løg fint og varm en gryde op med lidt olie ved middel varme. Tilsæt løgene og lad dem stege langsomt, indtil de bliver bløde og let gennemsigtige uden at tage farve.\n\nTilsæt hakket oksekød og skru lidt op for varmen. Del kødet godt med en ske og steg det, til det er gennemstegt og har fået lidt stegeskorpe. Krydr med salt og peber.\n\nTilsæt hakkede tomater og eventuelt lidt tomatpuré for ekstra fylde. Rør godt rundt og lad saucen simre ved lav varme i mindst 20–25 minutter. Rør jævnligt, så den ikke brænder på.\n\nSmag til med oregano, basilikum, salt og peber. Hvis saucen virker for syrlig, kan du tilsætte en smule sukker.\n\nKog spaghetti i rigeligt saltet vand efter anvisningen på pakken. Hæld vandet fra, fordel pastaen på tallerkener og server kødsaucen ovenpå. Drys evt. med revet parmesan før servering.",
            R.drawable.recipe_1
        )

        val r2 = recipe(
            "Chicken Wok", 30, "Hurtig asiatisk inspireret wokret med grønt",
            "Skær kyllingebryst i tynde strimler og dup dem tørre med køkkenrulle, så de steger bedre. Varm en wok eller stor pande op med lidt olie ved høj varme.\n\nSteg kyllingen i små portioner, så den bliver brunet i stedet for kogt. Tag den færdigstegte kylling af panden og læg den til side.\n\nTilsæt evt. lidt ekstra olie og steg grøntsagerne hurtigt, så de stadig er sprøde. Start med de hårdeste grøntsager og tilsæt de blødere til sidst.\n\nKom kyllingen tilbage på panden sammen med sojasauce og eventuelt lidt hvidløg eller ingefær. Vend det hele godt sammen i 1–2 minutter.\n\nSmag til med mere sojasauce hvis nødvendigt og server straks med ris eller nudler.",
            R.drawable.recipe_2
        )

        val r3 = recipe(
            "Chili con Carne", 50, "Fyldig og krydret gryderet",
            "Hak løg og hvidløg fint. Varm en stor gryde op med lidt olie og svits løg og hvidløg ved middel varme, til de bliver bløde og dufter godt.\n\nTilsæt hakket oksekød og steg det grundigt, mens du deler det med en ske, så der ikke er store klumper. Krydr med salt, peber og chili.\n\nTilsæt hakkede tomater, bønner og evt. lidt vand eller bouillon, så retten ikke bliver for tyk. Rør godt rundt.\n\nLad retten simre ved lav varme i mindst 30 minutter, så smagen bliver kraftigere. Rør jævnligt og justér konsistensen med lidt vand hvis nødvendigt.\n\nSmag til med mere chili, salt og evt. lidt sukker. Server med ris eller brød.",
            R.drawable.recipe_3
        )

        val r4 = recipe(
            "Pasta Alfredo", 25, "Cremet pastaret med ostesauce",
            "Bring en stor gryde med saltet vand i kog og kog pastaen efter anvisningen på pakken, til den er al dente.\n\nImens varmes fløden op i en pande ved middel varme. Undgå at koge fløden, da den kan skille.\n\nTilsæt den revne ost lidt ad gangen under omrøring, så saucen bliver jævn og cremet.\n\nHæld den kogte pasta direkte over i saucen sammen med lidt af pastavandet og vend det hele godt rundt.\n\nSmag til med salt og peber og server straks med ekstra ost på toppen.",
            R.drawable.recipe_4
        )

        val r5 = recipe(
            "Chicken Curry", 40, "Krydret karryret med kylling",
            "Skær kyllingen i mundrette tern. Varm olie op i en gryde og brun kyllingen godt på alle sider.\n\nDrys karry over kyllingen og svits krydderiet kort, så smagen frigives.\n\nTilsæt fløde eller kokosmælk og rør godt rundt. Skru ned for varmen og lad retten simre i ca. 20 minutter.\n\nHvis saucen bliver for tyk, kan du tilsætte lidt vand eller bouillon. Smag til med salt.\n\nServer med ris og evt. frisk koriander på toppen.",
            R.drawable.recipe_5
        )

        val r6 = recipe(
            "Wraps med kylling", 20, "Let og hurtig aftensmad med saftig kylling",
            "Skær kyllingen i strimler og krydr med salt, peber og evt. paprika. Varm olie op på en pande ved middelhøj varme og steg kyllingen gylden og gennemstegt.\n\nSkær grøntsager i tynde strimler og varm dem kort på panden sammen med kyllingen, så de stadig har bid.\n\nVarm wraps kort på en tør pande eller i ovnen, så de bliver bløde.\n\nFordel fyldet på wraps, rul dem sammen og server straks.",
            R.drawable.recipe_6
        )

        val r7 = recipe(
            "Veggie Wok", 25, "Sprød grøntsagswok med asiatisk smag",
            "Skær alle grøntsager i ensartede stykker, så de steger jævnt. Varm en wok eller stor pande op med olie ved høj varme.\n\nSteg først de grøntsager der tager længst tid, og tilsæt de mere sarte til sidst.\n\nTilsæt sojasauce og evt. lidt hvidløg eller chili. Vend hurtigt rundt så saucen fordeles.\n\nServer straks med ris eller nudler.",
            R.drawable.recipe_7
        )

        val r8 = recipe(
            "frikadelle med tilbehør", 35, "Mættende ret med kød og grøntsager",
            "Kog ris efter anvisningen på pakken og hold dem varme.\n\nSteg oksekødet på en pande ved høj varme, så det får stegeskorpe. Tag kødet af panden.\n\nSteg løg og grøntsager på samme pande, tilsæt kødet igen og bland risene i.\n\nVarm det hele godt igennem og smag til med salt og peber.",
            R.drawable.recipe_8
        )

        val r9 = recipe(
            "Tomatsuppe", 30, "Blød og varmende suppe",
            "Svits løg i en gryde med lidt olie til de er bløde. Tilsæt tomater og evt. bouillon.\n\nLad suppen simre i 15 minutter.\n\nBlend suppen glat og tilsæt fløde hvis ønsket.\n\nSmag til med salt, peber og evt. sukker.",
            R.drawable.recipe_9
        )

        val r10 = recipe(
            "Laks med grønt", 35, "Let og sund fiskeret",
            "Krydr laksen med salt og peber. Steg den på en pande med olie ved middel varme, til den er netop gennemstegt.\n\nDamp eller steg grøntsager separat så de stadig er sprøde.\n\nServer laksen sammen med grøntsager og evt. kartofler.",
            R.drawable.recipe_10
        )

        val r11 = recipe(
            "Mørbrad gryde", 40, "Cremet gryderet med svinekød og svampe",
            "Skær svinemørbrad i skiver og brun dem i gryde med lidt olie. Tag kødet op og læg til side.\n\nSvits løg og svampe i samme gryde, så de afgiver væde og bliver gyldne.\n\nHæld fløde i gryden, kom kødet tilbage og lad retten simre i ca. 20 minutter.\n\nSmag til med salt og peber og server med ris eller kartofler.",
            R.drawable.recipe_11
        )

        val r12 = recipe(
            "Frikadeller", 40, "Dansk klassiker med sprøde frikadeller",
            "Rør fars med æg, finthakket løg, salt og peber. Lad farsen hvile 5 minutter.\n\nForm små frikadeller med en ske og steg dem i rigeligt smør på middel varme.\n\nVend dem jævnligt så de bliver gyldne hele vejen rundt.\n\nServer med kartofler og evt. rødkål.",
            R.drawable.recipe_12
        )

        val r13 = recipe(
            "Æbleskiver", 35, "Søde og bløde æbleskiver",
            "Varm æbleskivepanden op og kom lidt fedtstof i hvert hul.\n\nHæld dej i hullerne og læg en skefuld æbleskivemasse i midten.\n\nVend æbleskiverne gradvist med en pind så de bliver runde.\n\nBag dem gyldne og server med flormelis og syltetøj.",
            R.drawable.recipe_13
        )

        val r14 = recipe(
            "Stegt flæsk med kartofler", 45, "Dansk nationalret med kartofler",
            "Steg flæsk sprødt på pande eller i ovn ved 200 grader.\n\nSteg løg i fedtet til de er bløde og let karamelliserede.\n\nKog kartofler møre.\n\nServer flæsk og løg med kartofler og evt. persillesovs.",
            R.drawable.recipe_14
        )

        val r15 = recipe(
            "Rugbrødsmørrebrød", 20, "Klassisk dansk frokost",
            "Smør rugbrød med smør i et jævnt lag.\n\nLæg skinke ovenpå og pynt evt. med grønt eller agurk.\n\nServer straks mens brødet stadig er friskt.",
            R.drawable.recipe_15
        )

        val r16 = recipe(
            "Rødkål gryde", 50, "Langtidskogt rødkål med sødme",
            "Snit rødkål fint og svits i gryde med smør.\n\nTilsæt lidt væske og lad kålen simre under låg i 40 minutter.\n\nSmag til med salt og evt. sukker.\n\nServer som tilbehør til kødretter.",
            R.drawable.recipe_16
        )

        val r17 = recipe(
            "Medister med kartofler", 35, "Klassisk dansk medisterpølse",
            "Steg medister langsomt på pande så den bliver gennemstegt.\n\nKog kartofler møre.\n\nServer med brun sovs og evt. rødkål.",
            R.drawable.recipe_17
        )

        val r18 = recipe(
            "Karbonader", 40, "Panerede bøffer med sovs",
            "Form flade bøffer af farsen og vend dem i mel.\n\nSteg dem gyldne på pande.\n\nLav brun sovs i panden af stegeskyen.\n\nServer med kartofler og grøntsager.",
            R.drawable.recipe_18
        )

        val r19 = recipe(
            "Pølser med kartofler", 30, "Nem hverdagsret",
            "Kog kartofler møre.\n\nSteg eller kog pølser efter ønske.\n\nServer med sennep og evt. brun sovs.",
            R.drawable.recipe_19
        )

        val r20 = recipe(
            "Kylling i flødesauce", 45, "Cremet og fyldig kyllingeret",
            "Brun kyllingestykker i gryde med olie.\n\nTilsæt fløde og lad simre i 25 minutter.\n\nSmag til med salt og peber.\n\nServer med ris eller kartofler.",
            R.drawable.recipe_20
        )

        /* ---------- RECIPE ITEMS ---------- */

        recipeItemDao.insertAll(
            listOf(
                // r1 Spaghetti Bolognese
                RecipeItem(r1.toLong(), pasta.toLong(), 200),
                RecipeItem(r1.toLong(), beef.toLong(), 300),
                RecipeItem(r1.toLong(), tomato.toLong(), 200),
                RecipeItem(r1.toLong(), onion.toLong(), 100),

                // r2 Chicken Wok
                RecipeItem(r2.toLong(), chicken.toLong(), 300),
                RecipeItem(r2.toLong(), pepper.toLong(), 150),
                RecipeItem(r2.toLong(), soy.toLong(), 30),
                RecipeItem(r2.toLong(), carrot.toLong(), 100),

                // r3 Chili con Carne
                RecipeItem(r3.toLong(), beef.toLong(), 300),
                RecipeItem(r3.toLong(), beans.toLong(), 200),
                RecipeItem(r3.toLong(), tomato.toLong(), 200),
                RecipeItem(r3.toLong(), chili.toLong(), 5),
                RecipeItem(r3.toLong(), onion.toLong(), 100),

                // r4 Pasta Alfredo
                RecipeItem(r4.toLong(), pasta.toLong(), 200),
                RecipeItem(r4.toLong(), cream.toLong(), 200),
                RecipeItem(r4.toLong(), cheese.toLong(), 100),
                RecipeItem(r4.toLong(), butter.toLong(), 30),

                // r5 Chicken Curry
                RecipeItem(r5.toLong(), chicken.toLong(), 300),
                RecipeItem(r5.toLong(), curry.toLong(), 10),
                RecipeItem(r5.toLong(), cream.toLong(), 200),
                RecipeItem(r5.toLong(), rice.toLong(), 200),

                // r6 Wraps med kylling
                RecipeItem(r6.toLong(), wraps.toLong(), 4),
                RecipeItem(r6.toLong(), chicken.toLong(), 250),
                RecipeItem(r6.toLong(), pepper.toLong(), 100),
                RecipeItem(r6.toLong(), tomato.toLong(), 100),

                // r7 Veggie Wok
                RecipeItem(r7.toLong(), broccoli.toLong(), 150),
                RecipeItem(r7.toLong(), carrot.toLong(), 100),
                RecipeItem(r7.toLong(), pepper.toLong(), 100),
                RecipeItem(r7.toLong(), soy.toLong(), 30),

                // r8 Ris med oksekød
                RecipeItem(r8.toLong(), rice.toLong(), 200),
                RecipeItem(r8.toLong(), beef.toLong(), 300),
                RecipeItem(r8.toLong(), onion.toLong(), 100),
                RecipeItem(r8.toLong(), carrot.toLong(), 100),

                // r9 Tomatsuppe
                RecipeItem(r9.toLong(), tomato.toLong(), 400),
                RecipeItem(r9.toLong(), cream.toLong(), 100),
                RecipeItem(r9.toLong(), onion.toLong(), 80),

                // r10 Laks med grønt
                RecipeItem(r10.toLong(), fish.toLong(), 300),
                RecipeItem(r10.toLong(), broccoli.toLong(), 150),
                RecipeItem(r10.toLong(), potatoes.toLong(), 300),

                // r11 Mørbrad gryde
                RecipeItem(r11.toLong(), pork.toLong(), 400),
                RecipeItem(r11.toLong(), mushrooms.toLong(), 150),
                RecipeItem(r11.toLong(), cream.toLong(), 200),
                RecipeItem(r11.toLong(), onion.toLong(), 100),

                // r12 Frikadeller
                RecipeItem(r12.toLong(), groundMeat.toLong(), 400),
                RecipeItem(r12.toLong(), eggs.toLong(), 1),
                RecipeItem(r12.toLong(), onion.toLong(), 80),
                RecipeItem(r12.toLong(), redCabbage.toLong(), 150),

                // r13 Æbleskiver
                RecipeItem(r13.toLong(), flour.toLong(), 200),
                RecipeItem(r13.toLong(), eggs.toLong(), 2),
                RecipeItem(r13.toLong(), milk.toLong(), 200),
                RecipeItem(r13.toLong(), appleFilling.toLong(), 150),

                // r14 Stegt flæsk og løg
                RecipeItem(r14.toLong(), porkFlank.toLong(), 300),
                RecipeItem(r14.toLong(), potatoes.toLong(), 400),
                RecipeItem(r14.toLong(), onion.toLong(), 150),

                // r15 Rugbrødsmørrebrød
                RecipeItem(r15.toLong(), bread.toLong(), 2),
                RecipeItem(r15.toLong(), ham.toLong(), 100),
                RecipeItem(r15.toLong(), butter.toLong(), 20),

                // r16 Rødkål gryde
                RecipeItem(r16.toLong(), redCabbage.toLong(), 400),
                RecipeItem(r16.toLong(), butter.toLong(), 30),
                RecipeItem(r16.toLong(), appleFilling.toLong(), 50),

                // r17 Medister med kartofler
                RecipeItem(r17.toLong(), pork.toLong(), 400),
                RecipeItem(r17.toLong(), potatoes.toLong(), 400),
                RecipeItem(r17.toLong(), redCabbage.toLong(), 150),

                // r18 Karbonader
                RecipeItem(r18.toLong(), groundMeat.toLong(), 400),
                RecipeItem(r18.toLong(), eggs.toLong(), 1),
                RecipeItem(r18.toLong(), potatoes.toLong(), 300),

                // r19 Pølser med kartofler
                RecipeItem(r19.toLong(), pork.toLong(), 300),
                RecipeItem(r19.toLong(), potatoes.toLong(), 400),

                // r20 Kylling i flødesauce
                RecipeItem(r20.toLong(), chicken.toLong(), 350),
                RecipeItem(r20.toLong(), cream.toLong(), 200),
                RecipeItem(r20.toLong(), rice.toLong(), 200),
            )
        )

        /* ---------- SHOPPING LIST ---------- */

        val shoppingListId = shoppingListDao.insert(ShoppingList(name = "Weekly groceries")).toInt()

        shoppingListItemGroupDao.insert(
            listOf(
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = pasta.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 500f,
                    isChecked = false
                ),
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = rice.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 500f,
                    isChecked = false
                ),

                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = beef.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 300f,
                    isChecked = false
                ),
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = chicken.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 300f,
                    isChecked = false
                ),

                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = potatoes.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 500f,
                    isChecked = false
                ),
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = onion.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 300f,
                    isChecked = false
                ),

                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = milk.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 1000f,
                    isChecked = false
                ),
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = eggs.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 10f,
                    isChecked = false
                ),

                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = bread.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 1f,
                    isChecked = false
                ),
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = butter.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 200f,
                    isChecked = false
                ),

                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = tomato.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 300f,
                    isChecked = false
                ),
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = cheese.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 200f,
                    isChecked = false
                ),

                // Fisk (Fish) category
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = fish.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 300f,
                    isChecked = false
                ),
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = herring.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 150f,
                    isChecked = false
                ),

                // Krydderier (Spices) category
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = curry.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 50f,
                    isChecked = false
                ),
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = chili.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 30f,
                    isChecked = false
                ),

                // Kolonial category
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = soy.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 250f,
                    isChecked = false
                ),
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = beans.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 400f,
                    isChecked = false
                ),
                ShoppingListItemGroup(
                    shoppingListId = shoppingListId.toLong(),
                    itemGroupId = corn.toLong(),
                    recipeId = null,
                    portionQuantity = 1,
                    portionSize = 300f,
                    isChecked = false
                )
            )
        )
    }
}
