package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.Item
import com.example.a62550_foodapp.db.projection.ItemWithPrice
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Insert
    suspend fun insert(item: Item): Long

    @Query("SELECT COUNT(*) FROM items")
    suspend fun count(): Int

    @Query("SELECT * FROM items")
    fun getAll(): Flow<List<Item>>


    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Int): Item?

}