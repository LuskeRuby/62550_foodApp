package com.example.a62550_foodapp

import androidx.compose.ui.graphics.Color
import org.junit.Test
import org.junit.Assert.*
import com.example.a62550_foodapp.viewmodel.ThemeViewModel
import com.example.a62550_foodapp.viewmodel.StoreFilterViewModel
import com.example.a62550_foodapp.viewmodel.SelectedItemGroup
import com.example.a62550_foodapp.model.ShoppingList
import com.example.a62550_foodapp.model.Item
import com.example.a62550_foodapp.db.entity.ShoppingList as ShoppingListEntity
import com.example.a62550_foodapp.db.entity.Item as ItemEntity
import com.example.a62550_foodapp.utils.ImageFileManager
import java.io.File

/**
 * Comprehensive Test Suite for the Food App
 * Tests: ViewModels, Models, Database entities, and Utility functions
 * Uses JUnit 4 for proper Android discovery
 * Covers edge cases and general logic - 48+ test methods
 */

// ============================================================
// ## THEME VIEW MODEL TESTS
// ============================================================

// Don't test colors!!!!!

// ============================================================
// ## STORE FILTER VIEW MODEL TESTS
// ============================================================
class StoreFilterViewModelTest {

    @Test
    fun testInitialState() {
        val viewModel = StoreFilterViewModel()

        assertEquals(emptySet<Long>(), viewModel.selectedStores.value)
    }

    @Test
    fun testToggleStoreAddition() {
        val viewModel = StoreFilterViewModel()

        viewModel.toggleStore(1L)
        assertEquals(setOf(1L), viewModel.selectedStores.value)

        viewModel.toggleStore(2L)
        assertEquals(setOf(1L, 2L), viewModel.selectedStores.value)
    }

    @Test
    fun testToggleStoreRemoval() {
        val viewModel = StoreFilterViewModel()

        viewModel.toggleStore(1L)
        viewModel.toggleStore(2L)

        viewModel.toggleStore(1L)
        assertEquals(setOf(2L), viewModel.selectedStores.value)
    }

    @Test
    fun testSetSelectedStores() {
        val viewModel = StoreFilterViewModel()

        val newStores = setOf(5L, 10L, 15L)
        viewModel.setSelectedStores(newStores)
        assertEquals(newStores, viewModel.selectedStores.value)
    }

    @Test
    fun testSetSelectedStoresReplaces() {
        val viewModel = StoreFilterViewModel()

        viewModel.toggleStore(1L)
        viewModel.toggleStore(2L)

        viewModel.setSelectedStores(setOf(99L))
        assertEquals(setOf(99L), viewModel.selectedStores.value)
    }

    @Test
    fun testClearFilter() {
        val viewModel = StoreFilterViewModel()

        viewModel.toggleStore(1L)
        viewModel.toggleStore(2L)
        viewModel.toggleStore(3L)

        viewModel.clear()
        assertEquals(emptySet<Long>(), viewModel.selectedStores.value)
    }

    @Test
    fun testToggleSameStoreMultipleTimes() {
        val viewModel = StoreFilterViewModel()

        viewModel.toggleStore(1L)
        assertEquals(setOf(1L), viewModel.selectedStores.value)

        viewModel.toggleStore(1L)
        assertEquals(emptySet<Long>(), viewModel.selectedStores.value)

        viewModel.toggleStore(1L)
        assertEquals(setOf(1L), viewModel.selectedStores.value)
    }

    @Test
    fun testMultipleStoresInteraction() {
        val viewModel = StoreFilterViewModel()

        viewModel.toggleStore(1L)
        viewModel.toggleStore(2L)
        viewModel.toggleStore(3L)
        assertEquals(setOf(1L, 2L, 3L), viewModel.selectedStores.value)

        viewModel.toggleStore(2L)
        assertEquals(setOf(1L, 3L), viewModel.selectedStores.value)

        viewModel.setSelectedStores(setOf(100L))
        assertEquals(setOf(100L), viewModel.selectedStores.value)
    }

    @Test
    fun testEmptySetToggle() {
        val viewModel = StoreFilterViewModel()

        assertTrue(viewModel.selectedStores.value.isEmpty())
        viewModel.toggleStore(5L)
        assertFalse(viewModel.selectedStores.value.isEmpty())
        viewModel.toggleStore(5L)
        assertTrue(viewModel.selectedStores.value.isEmpty())
    }
}

// ============================================================
// ## SHOPPING LIST MODEL TESTS
// ============================================================
class ShoppingListModelTest {

    @Test
    fun testShoppingListCreation() {
        val shoppingList = ShoppingList(
            id = 1L,
            name = "Weekly Groceries"
        )

        assertEquals(1L, shoppingList.id)
        assertEquals("Weekly Groceries", shoppingList.name)
    }

    @Test
    fun testShoppingListEquality() {
        val list1 = ShoppingList(1L, "Groceries")
        val list2 = ShoppingList(1L, "Groceries")

        assertEquals(list1, list2)
    }

    @Test
    fun testShoppingListInequality() {
        val list1 = ShoppingList(1L, "Groceries")
        val list2 = ShoppingList(2L, "Groceries")

        assertNotEquals(list1, list2)
    }

    @Test
    fun testShoppingListDifferentNames() {
        val list1 = ShoppingList(1L, "Groceries")
        val list2 = ShoppingList(1L, "Other Items")

        assertNotEquals(list1, list2)
    }

    @Test
    fun testShoppingListIdEdgeCases() {
        val list1 = ShoppingList(0L, "Test")
        val list2 = ShoppingList(Long.MAX_VALUE, "Test")

        assertEquals(0L, list1.id)
        assertEquals(Long.MAX_VALUE, list2.id)
    }

    @Test
    fun testShoppingListEmptyName() {
        val shoppingList = ShoppingList(1L, "")

        assertEquals("", shoppingList.name)
    }

    @Test
    fun testShoppingListSpecialCharacters() {
        val shoppingList = ShoppingList(1L, "Groceries & Items #1!")

        assertEquals("Groceries & Items #1!", shoppingList.name)
    }

    @Test
    fun testShoppingListUnicodeCharacters() {
        val shoppingList = ShoppingList(1L, "Äpfel 🍎 Μήλα")

        assertEquals("Äpfel 🍎 Μήλα", shoppingList.name)
    }

    @Test
    fun testShoppingListLongName() {
        val longName = "A".repeat(1000)
        val shoppingList = ShoppingList(1L, longName)

        assertEquals(longName, shoppingList.name)
    }
}

// ============================================================
// ## ITEM MODEL TESTS
// ============================================================
class ItemModelTest {

    @Test
    fun testItemCreation() {
        val item = Item(
            id = 1,
            itemGroupId = 5,
            category = "Vegetables",
            name = "Tomato",
            size = 250.0f,
            unitType = "g",
            imagePath = "/path/to/image.jpg"
        )

        assertEquals(1, item.id)
        assertEquals(5, item.itemGroupId)
        assertEquals("Vegetables", item.category)
        assertEquals("Tomato", item.name)
        assertEquals(250.0f, item.size)
        assertEquals("g", item.unitType)
        assertEquals("/path/to/image.jpg", item.imagePath)
    }

    @Test
    fun testItemNullImagePath() {
        val item = Item(
            id = 1,
            itemGroupId = 5,
            category = "Vegetables",
            name = "Tomato",
            size = 250.0f,
            unitType = "g",
            imagePath = null
        )

        assertNull(item.imagePath)
    }

    @Test
    fun testItemEquality() {
        val item1 = Item(1, 5, "Vegetables", "Tomato", 250.0f, "g", "/path")
        val item2 = Item(1, 5, "Vegetables", "Tomato", 250.0f, "g", "/path")

        assertEquals(item1, item2)
    }

    @Test
    fun testItemSizeEdgeCases() {
        val item1 = Item(1, 5, "Vegetables", "Tomato", 0.0f, "g", null)
        val item2 = Item(1, 5, "Vegetables", "Tomato", 99999.99f, "kg", null)

        assertEquals(0.0f, item1.size)
        assertEquals(99999.99f, item2.size)
    }

    @Test
    fun testItemDifferentCategories() {
        val vegetables = Item(1, 5, "Vegetables", "Tomato", 250.0f, "g", null)
        val meat = Item(2, 10, "Meat", "Chicken", 500.0f, "g", null)

        assertNotEquals(vegetables, meat)
    }

    @Test
    fun testItemNegativeSize() {
        val item = Item(1, 5, "Vegetables", "Tomato", -100.0f, "g", null)

        assertEquals(-100.0f, item.size)
    }

    @Test
    fun testItemDifferentUnitTypes() {
        val grams = Item(1, 5, "Vegetables", "Flour", 500.0f, "g", null)
        val kilograms = Item(2, 5, "Grains", "Rice", 1.5f, "kg", null)
        val milliliters = Item(3, 5, "Oils", "Oil", 500.0f, "ml", null)

        assertEquals("g", grams.unitType)
        assertEquals("kg", kilograms.unitType)
        assertEquals("ml", milliliters.unitType)
    }
}

// ============================================================
// ## DATABASE ENTITY TESTS
// ============================================================
class ShoppingListEntityTest {

    @Test
    fun testEntityCreation() {
        val entity = ShoppingListEntity(
            id = 1L,
            name = "Weekly Shopping"
        )

        assertEquals(1L, entity.id)
        assertEquals("Weekly Shopping", entity.name)
    }

    @Test
    fun testEntityDefaultId() {
        val entity = ShoppingListEntity(
            name = "Default ID Test"
        )

        assertEquals(0L, entity.id)
        assertEquals("Default ID Test", entity.name)
    }

    @Test
    fun testEntityEquality() {
        val entity1 = ShoppingListEntity(1L, "Test")
        val entity2 = ShoppingListEntity(1L, "Test")

        assertEquals(entity1, entity2)
    }

    @Test
    fun testEntityAutoGeneration() {
        val entity1 = ShoppingListEntity(0, "Test")
        val entity2 = ShoppingListEntity(0, "Another")

        assertEquals(0L, entity1.id)
        assertEquals(0L, entity2.id)
    }

    @Test
    fun testEntityDifferentNames() {
        val entity1 = ShoppingListEntity(1L, "Test")
        val entity2 = ShoppingListEntity(1L, "Different")

        assertNotEquals(entity1, entity2)
    }
}

class ItemEntityTest {

    @Test
    fun testItemEntityCreation() {
        val item = ItemEntity(
            id = 1L,
            itemGroupId = 5L,
            name = "Tomato",
            size = 250.0f,
            unitType = "g",
            imagePath = "/path/to/image.jpg"
        )

        assertEquals(1L, item.id)
        assertEquals(5L, item.itemGroupId)
        assertEquals("Tomato", item.name)
        assertEquals(250.0f, item.size)
        assertEquals("g", item.unitType)
        assertEquals("/path/to/image.jpg", item.imagePath)
    }

    @Test
    fun testItemEntityDefaultImage() {
        val item = ItemEntity(
            id = 1L,
            itemGroupId = 5L,
            name = "Tomato",
            size = 250.0f,
            unitType = "g"
        )

        assertNull(item.imagePath)
    }

    @Test
    fun testItemEntityDifferentUnitTypes() {
        val grams = ItemEntity(1L, 5L, "Tomato", 250.0f, "g", null)
        val kilograms = ItemEntity(2L, 5L, "Rice", 1.5f, "kg", null)
        val milliliters = ItemEntity(3L, 5L, "Milk", 500.0f, "ml", null)

        assertEquals("g", grams.unitType)
        assertEquals("kg", kilograms.unitType)
        assertEquals("ml", milliliters.unitType)
    }

    @Test
    fun testItemEntityForeignKeyReference() {
        val item = ItemEntity(1L, 100L, "Tomato", 250.0f, "g", null)

        assertEquals(100L, item.itemGroupId)
    }

    @Test
    fun testItemEntityZeroSize() {
        val item = ItemEntity(1L, 5L, "Tomato", 0.0f, "g", null)

        assertEquals(0.0f, item.size)
    }

    @Test
    fun testItemEntityLargeSize() {
        val item = ItemEntity(1L, 5L, "Tomato", Float.MAX_VALUE, "g", null)

        assertEquals(Float.MAX_VALUE, item.size)
    }
}

// ============================================================
// ## UTILITY FUNCTION TESTS
// ============================================================
class ImageFileManagerTest {

    @Test
    fun testDeleteIfUnderWithNullPath() {
        val filesDir = File("/tmp")
        val result = ImageFileManager.deleteIfUnder(filesDir, null)

        assertFalse(result)
    }

    @Test
    fun testDeleteIfUnderWithNonexistentFile() {
        val filesDir = File("/tmp")
        val result = ImageFileManager.deleteIfUnder(
            filesDir,
            "/tmp/nonexistent_file_${System.currentTimeMillis()}.jpg"
        )

        assertFalse(result)
    }

    @Test
    fun testDeleteIfUnderValidation() {
        val tempDir = File.createTempFile("test", "").apply { delete(); mkdir() }
        val tempFile = File(tempDir, "test_image.jpg").apply { createNewFile() }

        try {
            assertTrue(tempFile.exists())

            val result = ImageFileManager.deleteIfUnder(
                tempDir,
                tempFile.absolutePath
            )

            assertTrue(result)
            assertFalse(tempFile.exists())
        } finally {
            tempDir.delete()
        }
    }

    @Test
    fun testDeleteIfUnderCanonicalPath() {
        val tempDir = File.createTempFile("test", "").apply { delete(); mkdir() }
        val tempFile = File(tempDir, "test_image.jpg").apply { createNewFile() }

        try {
            val canonicalPath = tempFile.canonicalPath
            val result = ImageFileManager.deleteIfUnder(tempDir, canonicalPath)

            assertTrue(result)
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun testDeleteIfUnderOutsideDirectory() {
        val tempDir1 = File.createTempFile("test1", "").apply { delete(); mkdir() }
        val tempDir2 = File.createTempFile("test2", "").apply { delete(); mkdir() }
        val tempFile = File(tempDir2, "test.jpg").apply { createNewFile() }

        try {
            val result = ImageFileManager.deleteIfUnder(tempDir1, tempFile.absolutePath)

            assertFalse(result)
            assertTrue(tempFile.exists())
        } finally {
            tempDir1.delete()
            tempDir2.deleteRecursively()
        }
    }

    @Test
    fun testDeleteIfUnderSecurityCheck() {
        val allowedDir = File.createTempFile("allowed", "").apply { delete(); mkdir() }

        try {
            val result = ImageFileManager.deleteIfUnder(allowedDir, "/etc/passwd")
            assertFalse(result)
        } finally {
            allowedDir.delete()
        }
    }

    @Test
    fun testDeleteIfUnderWithEmptyString() {
        val filesDir = File("/tmp")
        val result = ImageFileManager.deleteIfUnder(filesDir, "")

        assertFalse(result)
    }
}

// ============================================================
// ## SELECTED ITEM GROUP DTO TESTS
// ============================================================
class SelectedItemGroupTest {

    @Test
    fun testSelectedItemGroupCreation() {
        val group = SelectedItemGroup(
            itemGroupId = 1L,
            quantity = 5
        )

        assertEquals(1L, group.itemGroupId)
        assertEquals(5, group.quantity)
    }

    @Test
    fun testSelectedItemGroupEdgeCaseQuantity() {
        val zero = SelectedItemGroup(1L, 0)
        val large = SelectedItemGroup(1L, 999999)

        assertEquals(0, zero.quantity)
        assertEquals(999999, large.quantity)
    }

    @Test
    fun testSelectedItemGroupEquality() {
        val group1 = SelectedItemGroup(1L, 5)
        val group2 = SelectedItemGroup(1L, 5)

        assertEquals(group1, group2)
    }

    @Test
    fun testSelectedItemGroupInequality() {
        val group1 = SelectedItemGroup(1L, 5)
        val group2 = SelectedItemGroup(2L, 5)

        assertNotEquals(group1, group2)
    }

    @Test
    fun testSelectedItemGroupNegativeQuantity() {
        val group = SelectedItemGroup(1L, -10)

        assertEquals(-10, group.quantity)
    }

    @Test
    fun testSelectedItemGroupMaxValues() {
        val group = SelectedItemGroup(Long.MAX_VALUE, Int.MAX_VALUE)

        assertEquals(Long.MAX_VALUE, group.itemGroupId)
        assertEquals(Int.MAX_VALUE, group.quantity)
    }
}
