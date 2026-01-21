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

        val onion = g("Løg", "Grøntsager", "g")
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

        val fallbackGroup = g("Mystisk ingrediens", "Ukendt", "g")

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
            item(flour, "Hvedemel 1 kg", 1000f, "g")
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
            "Spaghetti Bolognese", 45, "Italiensk klassiker",
            "Kog spaghetti efter anvisning på pakken. Svits løg i lidt olie, tilsæt hakket oksekød og brun det godt. Tilsæt tomater og lad saucen simre i 20–30 minutter. Smag til med salt og peber. Server saucen over pastaen.",
            R.drawable.recipe_1
        )

        val r2 = recipe(
            "Chicken Wok", 30, "Asiatisk wok",
            "Skær kylling i strimler og steg i varm pande med olie. Tilsæt grøntsager og steg kort. Hæld sojasauce over og vend det hele rundt. Server med ris eller nudler.",
            R.drawable.recipe_2
        )

        val r3 = recipe(
            "Chili con Carne", 50, "Krydret gryderet",
            "Svits løg og hvidløg i gryde. Tilsæt hakket oksekød og brun det. Kom tomater, bønner og krydderier i. Lad retten simre i ca. 30 minutter. Smag til med salt og chili.",
            R.drawable.recipe_3
        )

        val r4 = recipe(
            "Pasta Alfredo", 25, "Cremet pasta",
            "Kog pasta. Varm fløde op i pande og tilsæt revet ost. Rør til cremet sauce. Vend pastaen i saucen og smag til med salt og peber.",
            R.drawable.recipe_4
        )

        val r5 = recipe(
            "Chicken Curry", 40, "Karryret",
            "Steg kylling i tern i gryde. Tilsæt karry og svits kort. Hæld fløde eller kokosmælk i og lad simre i 20 minutter. Server med ris.",
            R.drawable.recipe_5
        )

        val r6 = recipe(
            "Wraps med kylling", 20, "Let aftensmad",
            "Steg kylling med krydderier. Varm wraps. Fyld wraps med kylling og grøntsager. Rul sammen og server.",
            R.drawable.recipe_6
        )

        val r7 = recipe(
            "Veggie Wok", 25, "Grøntsagswok",
            "Varm olie i pande. Steg alle grøntsager hurtigt ved høj varme. Tilsæt sojasauce og evt. chili. Server med ris eller nudler.",
            R.drawable.recipe_7
        )

        val r8 = recipe(
            "Ris med oksekød", 35, "Hurtig hverdagsret",
            "Kog ris. Steg oksekød med løg i pande. Tilsæt grøntsager og steg videre. Bland risene i og varm det hele igennem.",
            R.drawable.recipe_8
        )

        val r9 = recipe(
            "Tomatsuppe", 30, "Varm suppe",
            "Kog tomater med lidt vand og bouillon. Blend suppen glat. Smag til med salt og peber og tilsæt evt. fløde før servering.",
            R.drawable.recipe_9
        )

        val r10 = recipe(
            "Laks med grønt", 35, "Sund fiskeret",
            "Steg laks på pande med lidt olie. Damp eller steg grøntsager separat. Server laksen sammen med grønt og evt. kartofler.",
            R.drawable.recipe_10
        )

        val r11 = recipe(
            "Mørbrad gryde", 40, "Lækkert stegt mel fra Sverige",
            "Skær svinemørbrad i skiver og brun i gryde. Tilsæt løg og svampe. Hæld fløde i og lad simre i 20 minutter. Smag til med salt og peber. Server med ris eller kartofler.",
            R.drawable.recipe_11
        )

        val r12 = recipe(
            "Frikadeller", 40, "Danske klassiker med rødkål",
            "Rør fars med æg, løg, salt og peber. Form frikadeller og steg dem gyldne på pande. Server med kartofler og rødkål.",
            R.drawable.recipe_12
        )

        val r13 = recipe(
            "Æbleskiver", 35, "Søde danske æbleskiver",
            "Varm æbleskivepande op og tilsæt fedtstof. Fyld dej i hullerne og læg æbleskivemasse i midten. Vend æbleskiverne under bagning til de er gyldne. Server med flormelis og syltetøj.",
            R.drawable.recipe_13
        )

        val r14 = recipe(
            "Stegt flæsk og løg", 45, "Dansk klassiker med kartofler",
            "Steg flæsk sprødt på pande eller i ovn. Steg løg bløde i fedtet. Kog kartofler. Server flæsk og løg med kartofler og persillesauce.",
            R.drawable.recipe_14
        )

        val r15 = recipe(
            "Rugbrødsmørrebrød", 20, "Let frokost med skinke",
            "Smør rugbrød med smør. Læg skinke ovenpå og pynt evt. med grønt. Server straks.",
            R.drawable.recipe_15
        )

        val r16 = recipe(
            "Rødkål gryde", 50, "Varm og behagelig ret",
            "Snit rødkål og svits i gryde med lidt fedtstof. Tilsæt væske og lad simre til kålen er mør. Smag til med salt og evt. lidt sukker.",
            R.drawable.recipe_16
        )

        val r17 = recipe(
            "Medister med kartofler", 35, "Klassisk dansk medisterpølse",
            "Steg medisterpølsen langsomt på pande til gennemstegt. Kog kartofler. Server med brun sovs og evt. rødkål.",
            R.drawable.recipe_17
        )

        val r18 = recipe(
            "Karbonader", 40, "Stegt kød med sauce",
            "Form hakket kød til flade bøffer og steg dem gyldne. Lav brun sovs i panden. Server med kartofler og grøntsager.",
            R.drawable.recipe_18
        )

        val r19 = recipe(
            "Pølser med kartofler", 30, "Dansk husmannskost",
            "Kog kartofler. Steg eller kog pølser. Server sammen med sennep og evt. brun sovs.",
            R.drawable.recipe_19
        )

        val r20 = recipe(
            "Kylling i flødesauce", 45, "Cremet og lækker",
            "Steg kylling i gryde. Tilsæt fløde og lad simre til kyllingen er mør. Smag til med salt og peber. Server med ris eller kartofler.",
            R.drawable.recipe_20
        )

        val fallbackRecipe = recipe(
            "Fallback Opskrift", 10, "Tester opskrift fallback uden konkrete items",
            "Denne opskrift bruges kun til test. Ingen konkrete ingredienser eller trin er angivet.",
            null
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

                // fallback
                RecipeItem(fallbackRecipe.toLong(), fallbackGroup.toLong(), 150),
                RecipeItem(fallbackRecipe.toLong(), fallbackGroup.toLong(), 75)
            )
        )

        /* ---------- SHOPPING LIST ---------- */

        val shoppingListId = shoppingListDao.insert(ShoppingList(name = "Weekly groceries")).toInt()

        shoppingListItemGroupDao.insert(
            ShoppingListItemGroup(
                shoppingListId = shoppingListId.toLong(),
                itemGroupId = fallbackGroup.toLong(),
                recipeId = null,
                portionQuantity = 2,
                portionSize = 100f,
                isChecked = false
            )
        )
    }
}
