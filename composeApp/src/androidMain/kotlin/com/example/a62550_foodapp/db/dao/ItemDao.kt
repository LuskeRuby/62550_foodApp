package com.example.a62550_foodapp.db.dao

import androidx.room.*
import com.example.a62550_foodapp.db.entity.Item
import com.example.a62550_foodapp.db.entity.ItemSupermarketCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert
    suspend fun insert(item: Item)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItemSupermarketCrossRef(crossRef: ItemSupermarketCrossRef)

    @Query("SELECT * FROM items")
    fun getAll(): Flow<List<Item>>
}
