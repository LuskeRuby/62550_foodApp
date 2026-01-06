package com.example.a62550_foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.a62550_foodapp.db.AppDatabase
import com.example.a62550_foodapp.db.DatabaseSeeder
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (BuildConfig.DEBUG) {
            val database: AppDatabase = get()

            lifecycleScope.launch {
                DatabaseSeeder.seed(database)
            }
        }

        setContent {
            App()
        }
    }
}
