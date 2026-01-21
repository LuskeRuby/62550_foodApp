package com.example.a62550_foodapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "recipe_items",
    primaryKeys = ["recipe_id", "item_group_id"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemGroup::class,
            parentColumns = ["id"],
            childColumns = ["item_group_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("item_group_id")]
)
data class RecipeItem(
    @ColumnInfo(name = "recipe_id")
    val recipeId: Long,

    @ColumnInfo(name = "item_group_id")
    val itemGroupId: Long,

    @ColumnInfo(name = "size_of_one_portion")
    val sizeOfOnePortion: Int
)
