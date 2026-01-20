package com.example.a62550_foodapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.a62550_foodapp.db.entity.ShoppingListItemGroup
import com.example.a62550_foodapp.db.projection.ShoppingListEntry
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing logical shopping list entries.
 *
 * This table does NOT store concrete store products, but instead stores
 * ingredient groups (ItemGroup) that should be bought, optionally linked
 * to a Recipe via recipeId.
 *
 * Prices and concrete items are resolved dynamically via joins with
 * items and item_weekly_prices tables, allowing real-time price comparison
 * across supermarkets without duplicating data.
 */
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

    /**
     * Calculates the total shopping list price for a specific supermarket.
     *
     * Prices are resolved dynamically by joining ingredient groups with
     * items and weekly prices, and selecting matching store prices only.
     *
     * Returns NULL if no prices are available.
     */
    @Query("""
SELECT SUM(
    CASE
        WHEN p.price IS NOT NULL
        THEN p.price * slig.quantity
        ELSE 0
    END
)
FROM shopping_list_item_groups slig
JOIN items i
    ON i.item_group_id = slig.item_group_id
LEFT JOIN item_weekly_prices p
    ON p.item_id = i.id
   AND p.supermarket_id = :supermarketId
WHERE slig.shopping_list_id = :shoppingListId
""")
    fun getTotalPriceByStoreFlow(
        shoppingListId: Int,
        supermarketId: Int
    ): Flow<Float?>



    /**
     * Returns shopping list entries resolved to concrete store products
     * using the cheapest available item per ingredient group in the selected stores.
     *
     * For each ingredient group, the cheapest item (by weekly price) in the
     * specified supermarket is selected via a subquery.
     *
     * This allows the UI to display:
     * - product name
     * - size and unit
     * - quantity needed
     * - correct store-specific price
     *
     * All values are delivered as a reactive Flow for real-time UI updates.
     */
    @Query("""
SELECT
    ig.id              AS itemGroupId,
    i.id               AS itemId,
    i.name             AS itemName,
    i.size             AS size,
    i.unitType         AS unitType,
    slig.quantity      AS quantity,
    p.price            AS price,
    slig.is_checked    AS isChecked,
    ig.category        AS category,
    slig.recipe_id     AS recipeId
FROM shopping_list_item_groups slig

JOIN item_groups ig
    ON ig.id = slig.item_group_id

JOIN items i
    ON i.item_group_id = ig.id

JOIN item_weekly_prices p
    ON p.item_id = i.id
   AND p.supermarket_id = :supermarketId

WHERE slig.shopping_list_id = :shoppingListId
  AND p.price = (
      SELECT MIN(p2.price)
      FROM item_weekly_prices p2
      JOIN items i2 ON i2.id = p2.item_id
      WHERE i2.item_group_id = ig.id
        AND p2.supermarket_id = :supermarketId
  )
""")
    fun getShoppingListEntriesByStoreFlow(
        shoppingListId: Int,
        supermarketId: Int
    ): Flow<List<ShoppingListEntry>>

}