package com.nazam.instaclone.feature.permissions.domain.model

/**
 * Permissions de publication d'un utilisateur.
 * Évolutif : on pourra ajouter d'autres flags (canComment, canVote, etc.)
 */
data class PostPermission(
    val canCreatePost: Boolean
) {
    companion object {
        val Denied = PostPermission(canCreatePost = false)
    }
}
