package com.example.a62550_foodapp.db

import com.example.a62550_foodapp.db.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seed(database: AppDatabase) = withContext(Dispatchers.IO) {

        // ---- IDPOTENT GUARD ----
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

        // ---- ITEM PRICES ----
        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = carrotsId.toInt(),
                year = 2024,
                week = 28,
                price = 7.0f,
                supermarket_id = nettoId.toInt()
            )
        )

        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = carrotsId.toInt(),
                year = 2024,
                week = 28,
                price = 8.5f,
                supermarket_id = kvicklyId.toInt()
            )
        )

        itemWeeklyPriceDao.insert(
            ItemWeeklyPrice(
                item_id = onionId.toInt(),
                year = 2024,
                week = 28,
                price = 12.0f,
                supermarket_id = nettoId.toInt()
            )
        )

        // ---- SHOPPING LIST ----
        val listId = shoppingListDao.insert(
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
