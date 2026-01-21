package com.example.a62550_foodapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.a62550_foodapp.db.entity.*
import com.example.a62550_foodapp.db.dao.*

@Database(
    entities = [
        ItemGroup::class,
        Item::class,
        ItemWeeklyPrice::class,
        Recipe::class,
        RecipeItem::class,
        ShoppingList::class,
        ShoppingListItemGroup::class,
        Supermarket::class
    ],
    version = 25,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemGroupDao(): ItemGroupDao
    abstract fun itemDao(): ItemDao
    abstract fun itemWeeklyPriceDao(): ItemWeeklyPriceDao

    abstract fun recipeDao(): RecipeDao
    abstract fun recipeItemDao(): RecipeItemDao

    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingListItemGroupDao(): ShoppingListItemGroupDao

    abstract fun supermarketDao(): SupermarketDao
}
