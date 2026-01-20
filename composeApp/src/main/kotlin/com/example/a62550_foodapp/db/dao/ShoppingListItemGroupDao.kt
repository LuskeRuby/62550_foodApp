package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.a62550_foodapp.db.entity.ShoppingListItemGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListItemGroupDao {

    @Insert
    suspend fun insert(item: ShoppingListItemGroup): Long

    @Insert
    suspend fun insert(items: List<ShoppingListItemGroup>): List<Long>

    @Update
    suspend fun update(item: ShoppingListItemGroup)

    //TODO avoid race condition when updating items
    @Update
    suspend fun update(items: List<ShoppingListItemGroup>)

    @Delete
    suspend fun delete(item: ShoppingListItemGroup)


    @Query("""
        SELECT *
        FROM shopping_list_item_groups
        WHERE shopping_list_id = :shoppingListId
    """)
    fun getItemGroupsMatchingListId(
        shoppingListId: Int
    ): Flow<List<ShoppingListItemGroup>>

    //update checkmark
    @Query("""
        UPDATE shopping_list_item_groups
        SET is_checked = :checked
        WHERE shopping_list_id = :shoppingListId
          AND item_group_id = :itemGroupId
          AND (recipe_id = :recipeId OR (recipe_id IS NULL AND :recipeId IS NULL))
    """)
    suspend fun updateCheckmark(
        shoppingListId: Int,
        itemGroupId: Int,
        recipeId: Int?,
        checked: Boolean
    )

    @Query("""
        DELETE FROM shopping_list_item_groups
        WHERE shopping_list_id = :shoppingListId
        AND item_group_id = :itemGroupId
        AND (recipe_id = :recipeId OR (recipe_id IS NULL AND :recipeId IS NULL))
    """)
    suspend fun delete(
        shoppingListId: Int,
        itemGroupId: Int,
        recipeId: Int?
    )

}