package com.nazam.instaclone.core.deeplink

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Stocke un deep link "en attente" (en mémoire).
 * ✅ Simple, KMP friendly
 */
object DeepLinkStore {

    private val _pendingPostId = MutableStateFlow<String?>(null)
    val pendingPostId: StateFlow<String?> = _pendingPostId

    fun onUrlReceived(url: String) {
        val result = DeepLinkParser.parse(url) ?: return
        _pendingPostId.value = result.postId
    }

    fun consumePostId(): String? {
        val value = _pendingPostId.value
        _pendingPostId.value = null
        return value
    }
}
