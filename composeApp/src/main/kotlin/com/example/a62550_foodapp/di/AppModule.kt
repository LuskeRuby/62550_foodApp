package com.example.a62550_foodapp.di

import android.app.Application
import androidx.room.Room
import com.example.a62550_foodapp.db.AppDatabase
import com.example.a62550_foodapp.viewmodel.RecipeViewModel
import com.example.a62550_foodapp.viewmodel.ShoppingListDetailsViewModel
import com.example.a62550_foodapp.viewmodel.ShoppingListViewModel
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.a62550_foodapp.api.MealDbApi
import com.example.a62550_foodapp.viewmodel.ApiRecipeDetailViewModel
import com.example.a62550_foodapp.viewmodel.DiscoverRecipeViewModel
import com.example.a62550_foodapp.viewmodel.ItemViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidLogger


/**
 * Call this once from Application.onCreate()
 */
fun startKoinApp(app: Application) {
    startKoin {
        androidLogger()
        androidContext(app)
        modules(appModule)
    }
}

val appModule = module {

    /* ---------- Database ---------- */

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "food_app.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /* ---------- DAOs ---------- */

    single { get<AppDatabase>().itemGroupDao() }
    single { get<AppDatabase>().itemDao() }
    single { get<AppDatabase>().itemWeeklyPriceDao() }

    single { get<AppDatabase>().recipeDao() }
    single { get<AppDatabase>().recipeItemDao() }

    single { get<AppDatabase>().shoppingListDao() }
    single { get<AppDatabase>().shoppingListItemDao() }
    single { get<AppDatabase>().shoppingListItemGroupDao() }

    single { get<AppDatabase>().supermarketDao() }

    /* ---------- API ---------- */

    single {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://themealdb.com/api/json/v1/1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    single {
        get<Retrofit>().create(MealDbApi::class.java)
    }

    /* ---------- ViewModels ---------- */

    viewModel {
        RecipeViewModel(
            recipeDao = get(),
            recipeItemDao = get(),
            itemWeeklyPriceDao = get(),
            appContext = androidContext(),
            itemGroupDao = get(),
            supermarketDao = get(),
            shoppingListItemGroupDao = get()
        )
    }

    viewModel{ ShoppingListViewModel(get()) }

    viewModel { (shoppingListId: Int) ->
        ShoppingListDetailsViewModel(
            shoppingListId = shoppingListId,
            shoppingListItemGroupDao = get()
        )
    }

    viewModel {
        DiscoverRecipeViewModel(
            api = get()
        )
    }

    viewModel {
        ApiRecipeDetailViewModel(
            api = get()
        )
    }

    viewModel { ThemeViewModel() }

    viewModel { ItemViewModel( itemDao = get() ) }

}
