package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_images")
data class QuizImage(
    @PrimaryKey val id: Int,
    val uri: String,
    val name: String,
    val colorsString: String, // Comma-separated colors (e.g. "orange,white")
    val scene: String,       // "indoor" or "outdoor"
    val sentiment: String,   // "happy", "sad", "neutral"
    val count: Int,
    val orientation: String, // "portrait" or "landscape"
    val category: String,    // "animal", "fruit", "animated_celebrity", "body_part", "stadium", "object"
    val tagsString: String   // Comma-separated tags (e.g. "cat,orange,sleeping,indoor")
) {
    val colors: List<String>
        get() = colorsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val tags: List<String>
        get() = tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
