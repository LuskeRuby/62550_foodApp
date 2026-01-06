package com.example.a62550_foodapp.db

import com.example.a62550_foodapp.db.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seed(database: AppDatabase) = withContext(Dispatchers.IO) {

        // ---- CLEAR DB FIRST ----
        database.clearAllTables()

        val itemDao = database.itemDao()
        val supermarketDao = database.supermarketDao()
        val shoppingListDao = database.shoppingListDao()
        val shoppingListItemDao = database.shoppingListItemDao()
        val itemWeeklyPriceDao = database.itemWeeklyPriceDao()

        // ---- SUPERMARKETS ----
        val nettoId = supermarketDao.insertAndReturnId(
            Supermarket(name = "Netto", logo = null)
        )

        val kvicklyId = supermarketDao.insertAndReturnId(
            Supermarket(name = "Kvickly", logo = null)
        )

        // ---- ITEMS ----
        val carrotsId = itemDao.insertAndReturnId(
            Item(name = "Gulerødder", unit = "500g", itemgroup = 1, picture = null)
        )

        val onionId = itemDao.insertAndReturnId(
            Item(name = "Løg", unit = "1 kg", itemgroup = 1, picture = null)
        )

        // ---- ITEM PRICES ----
        itemWeeklyPriceDao.insert(ItemWeeklyPrice(carrotsId.toInt(), 2024, 28, 7.0f, nettoId.toInt()))
        itemWeeklyPriceDao.insert(ItemWeeklyPrice(carrotsId.toInt(), 2024, 28, 8.5f, kvicklyId.toInt()))
        itemWeeklyPriceDao.insert(ItemWeeklyPrice(onionId.toInt(), 2024, 28, 12.0f, nettoId.toInt()))

        // ---- SHOPPING LIST ----
        val listId = shoppingListDao.insertAndReturnId(
            ShoppingList(name = "Aftensmad")
        )

        shoppingListItemDao.insert(
            ShoppingListItem(
                shopping_list_id = listId.toInt(),
                item_id = carrotsId.toInt(),
                quantity = 1.0f,
                is_checked = false,
                label = ""
            )
        )

        shoppingListItemDao.insert(
            ShoppingListItem(
                shopping_list_id = listId.toInt(),
                item_id = onionId.toInt(),
                quantity = 1.0f,
                is_checked = false,
                label = ""
            )
        )
    }
}
