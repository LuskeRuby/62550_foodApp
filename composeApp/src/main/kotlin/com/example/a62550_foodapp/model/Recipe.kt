package com.example.a62550_foodapp.model

// UI-facing data class
data class Recipe(
    val id: Int,
    val title: String,
    val description: String?,
    val instructions: String?,
    val picture: ByteArray?,
    val deletable: Boolean?,
    val items: List<RecipeItem> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (instructions != other.instructions) return false
        if (picture != null) {
            if (other.picture == null) return false
            if (!picture.contentEquals(other.picture)) return false
        } else if (other.picture != null) return false
        if (deletable != other.deletable) return false
        if (items != other.items) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (instructions?.hashCode() ?: 0)
        result = 31 * result + (picture?.contentHashCode() ?: 0)
        result = 31 * result + (deletable?.hashCode() ?: 0)
        result = 31 * result + items.hashCode()
        return result
    }
}
