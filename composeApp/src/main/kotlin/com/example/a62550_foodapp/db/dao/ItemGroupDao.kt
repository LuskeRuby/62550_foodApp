package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.a62550_foodapp.db.entity.ItemGroup
import kotlinx.coroutines.flow.Flow
@Dao
interface ItemGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itemGroup: ItemGroup): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(itemGroups: List<ItemGroup>)

    @Query("SELECT * FROM item_groups ORDER BY name ASC")
    fun getAllItemGroups(): Flow<List<ItemGroup>>

    @Query("SELECT * FROM item_groups WHERE id = :id")
    suspend fun getById(id: Int): ItemGroup?

    @Query("SELECT COUNT(*) FROM item_groups")
    suspend fun count(): Int

    @Query("DELETE FROM item_groups")
    suspend fun deleteAll()

    @Query("""
        SELECT category
        FROM item_groups
        WHERE id = :itemId
        LIMIT 1
    """)
    suspend fun getCategoryOfItem(itemId: Int): String?

}
