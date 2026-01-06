package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.Supermarket
import kotlinx.coroutines.flow.Flow

@Dao
interface SupermarketDao {
    @Insert
    suspend fun insert(supermarket: Supermarket)

    @Query("SELECT * FROM supermarkets")
    fun getAll(): Flow<List<Supermarket>>
}
