package com.example.a62550_foodapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.a62550_foodapp.db.entity.*
import com.example.a62550_foodapp.db.dao.*

@Database(
    entities = [
        ShoppingList::class,
        Item::class,
        ItemWeeklyPrice::class,
        Recipe::class,
        RecipeItem::class,
        ShoppingListItem::class,
        FoodItem::class,
        Supermarket::class,
        ItemSupermarketCrossRef::class
    ],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun itemDao(): ItemDao
    abstract fun recipeDao(): RecipeDao
    abstract fun recipeItemDao(): RecipeItemDao
    abstract fun shoppingListItemDao(): ShoppingListItemDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun supermarketDao(): SupermarketDao
}
