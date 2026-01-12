package com.example.a62550_foodapp.utils

import java.io.File

object ImageFileManager {
    /**
     * Delete the file at [path] if and only if it exists under [filesDir].
     * Returns true if the file was deleted, false otherwise.
     */
    fun deleteIfUnder(filesDir: File, path: String?): Boolean {
        if (path == null) return false
        return try {
            val f = File(path)
            if (f.exists() && f.canonicalPath.startsWith(filesDir.canonicalPath)) {
                f.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}

