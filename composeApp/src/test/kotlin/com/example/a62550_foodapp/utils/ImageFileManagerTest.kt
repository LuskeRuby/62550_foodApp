package com.example.a62550_foodapp.utils

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ImageFileManagerTest {

    @Test
    fun deleteIfUnder_deletesFileUnderFilesDir() {
        val tempDir = createTempDir(prefix = "app_files_")
        val file = File(tempDir, "recipe_test.jpg")
        file.writeText("dummy")

        val deleted = ImageFileManager.deleteIfUnder(tempDir, file.absolutePath)
        assertTrue("Expected file to be deleted", deleted)
        assertFalse("File should no longer exist", file.exists())

        // cleanup temp dir
        tempDir.deleteRecursively()
    }

    @Test
    fun deleteIfUnder_doesNotDeleteOutside() {
        val tempDir = createTempDir(prefix = "app_files_")
        val outsideDir = createTempDir(prefix = "outside_")
        val file = File(outsideDir, "outside.jpg")
        file.writeText("dummy")

        val deleted = ImageFileManager.deleteIfUnder(tempDir, file.absolutePath)
        assertFalse("Should not delete file outside filesDir", deleted)
        assertTrue("Outside file should still exist", file.exists())

        // cleanup
        outsideDir.deleteRecursively()
        tempDir.deleteRecursively()
    }
}

