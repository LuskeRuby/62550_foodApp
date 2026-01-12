package com.example.a62550_foodapp.api

import com.example.a62550_foodapp.api.dto.CategoryResponse
import com.example.a62550_foodapp.api.dto.MealListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealDbApi {

    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    @GET("filter.php")
    suspend fun getMealsByCategory(
        @Query("c") category: String
    ): MealListResponse

}
