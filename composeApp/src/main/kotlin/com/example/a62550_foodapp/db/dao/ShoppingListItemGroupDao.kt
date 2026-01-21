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
    suspend fun insert(items: List<ShoppingListItemGroup>)

    //TODO avoid race condition when updating items
    @Update
    suspend fun update(item: ShoppingListItemGroup)

    @Delete
    suspend fun delete(item: ShoppingListItemGroup)


    @Query("""
        SELECT *
        FROM shopping_list_item_groups
        WHERE shopping_list_id = :shoppingListId
    """)
    fun getItemGroupsMatchingListId(
        shoppingListId: Long
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
        shoppingListId: Long,
        itemGroupId: Long,
        recipeId: Long?,
        checked: Boolean
    )

    @Query("""
        DELETE FROM shopping_list_item_groups
        WHERE shopping_list_id = :shoppingListId
        AND item_group_id = :itemGroupId
        AND (recipe_id = :recipeId OR (recipe_id IS NULL AND :recipeId IS NULL))
    """)
    suspend fun delete(
        shoppingListId: Long,
        itemGroupId: Long,
        recipeId: Long?
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
        THEN p.price * slig.portion_quantity
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
        shoppingListId: Long,
        supermarketId: Long
    ): Flow<Float?>



    /**
     * Returns shopping list entries resolved to the CHEAPEST available product
     * per ingredient group, based on the selected supermarkets.
     *
     * Behavior:
     * - If storeIds is EMPTY → selects cheapest price across ALL supermarkets
     * - If storeIds contains values → selects cheapest price among those stores only
     *
     * This mirrors the same pricing logic used for recipe price calculation.
     *
     * For each ingredient group:
     * - joins all matching products (items)
     * - filters by selected supermarkets (if any)
     * - selects the MIN weekly price
     *
     * Returned values are reactive and update automatically when:
     * - shopping list changes
     * - prices change
     * - selected store filter changes
     */
    @Query("""
SELECT
    slig.id                 AS id,
    ig.id                   AS itemGroupId,
    i.id                    AS itemId,
    i.name                  AS itemName,
    i.size                  AS size,
    i.unitType              AS unitType,
    slig.portion_quantity   AS quantity,
    iwp.price               AS price,
    slig.is_checked         AS isChecked,
    ig.category             AS category,
    slig.recipe_id          AS recipeId,
    sm.name                 AS superMarketName
FROM shopping_list_item_groups slig

JOIN item_groups ig
    ON ig.id = slig.item_group_id

JOIN items i
    ON i.item_group_id = ig.id

JOIN item_weekly_prices iwp
    ON iwp.item_id = i.id
    
JOIN supermarkets sm
    ON sm.id = iwp.supermarket_id

WHERE slig.shopping_list_id = :shoppingListId

  AND (
        :storeCount = 0
        OR iwp.supermarket_id IN (:storeIds)
      )

  AND iwp.price = (
      SELECT MIN(p2.price)
      FROM item_weekly_prices p2
      JOIN items i2 ON i2.id = p2.item_id
      WHERE i2.item_group_id = ig.id
        AND (
              :storeCount = 0
              OR p2.supermarket_id IN (:storeIds)
            )
  )
""")
    fun getCheapestShoppingListEntriesFlow(
        shoppingListId: Long,
        storeIds: List<Long>,
        storeCount: Int
    ): Flow<List<ShoppingListEntry>>


    @Query("""
    SELECT COUNT(*)
    FROM shopping_list_item_groups
    WHERE shopping_list_id = :shoppingListId
      AND recipe_id = :recipeId
""")
    suspend fun recipeExistsInList(
        shoppingListId: Long,
        recipeId: Long
    ): Int

}