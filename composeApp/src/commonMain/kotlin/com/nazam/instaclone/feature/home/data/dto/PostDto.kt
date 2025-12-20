package com.nazam.instaclone.feature.home.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

@Serializable
data class PostDto(
    val id: String,
    val question: String? = null,
    val category: String? = null,
    val author_name: String? = null,
    val author_avatar: String? = null,
    val created_at: String? = null,
    val left_image: String? = null,
    val right_image: String? = null,
    val left_label: String? = null,
    val right_label: String? = null,
    val left_votes: Int = 0,
    val right_votes: Int = 0,
    val voted_left_ids: JsonElement? = null,
    val voted_right_ids: JsonElement? = null,
    val user_choice: String? = null
) {

    fun votedLeftList(): List<String> {
        return voted_left_ids
            ?.jsonArray
            ?.mapNotNull { it.toString().replace("\"", "") }
            ?: emptyList()
    }

    fun votedRightList(): List<String> {
        return voted_right_ids
            ?.jsonArray
            ?.mapNotNull { it.toString().replace("\"", "") }
            ?: emptyList()
    }
}