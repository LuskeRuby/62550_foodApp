package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.a62550_foodapp.db.entity.ItemWeeklyPrice

@Dao
interface ItemWeeklyPriceDao {
    @Insert
    suspend fun insert(itemWeeklyPrice: ItemWeeklyPrice)
}
