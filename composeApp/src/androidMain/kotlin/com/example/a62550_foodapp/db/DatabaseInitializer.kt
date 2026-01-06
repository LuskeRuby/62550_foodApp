package com.example.a62550_foodapp.db

import com.example.a62550_foodapp.db.dao.RecipeDao
import com.example.a62550_foodapp.db.entity.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DatabaseInitializer(private val recipeDao: RecipeDao) {

    fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if we already have recipes
            val existingRecipes = recipeDao.getAllRecipes().first()

            if (existingRecipes.isEmpty()) {
                val testRecipes = listOf(
                    Recipe(
                        title = "Pancakes",
                        description = "Fluffy breakfast",
                        instructions = "Mix flour, milk, eggs. Cook on pan.",
                        imagePath = null, // No image yet
                        deletable = false
                    ),
                    Recipe(
                        title = "Salad",
                        description = "Healthy lunch",
                        instructions = "Chop lettuce, tomatoes, cucumbers. Add dressing.",
                        imagePath = null,
                        deletable = false
                    )
                )
                recipeDao.insertAll(testRecipes)
                println("DatabaseInitializer: Inserted test recipes!")
            } else {
                println("DatabaseInitializer: Recipes already exist, skipping.")
            }
        }
    }
}
