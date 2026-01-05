package com.example.a62550_foodapp.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FoodItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
}
