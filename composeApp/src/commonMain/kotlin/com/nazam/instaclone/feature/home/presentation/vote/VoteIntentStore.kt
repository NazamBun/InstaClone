package com.nazam.instaclone.feature.home.presentation.vote

/**
 * Stockage en mémoire (KMP friendly) pour retenir un vote
 * qu'un utilisateur a voulu faire AVANT de se connecter.
 *
 * - save(...) : on enregistre l'intention
 * - consume() : on récupère puis on efface (anti boucle)
 */
object VoteIntentStore {

    enum class Side { LEFT, RIGHT }

    data class VoteIntent(
        val postId: String,
        val side: Side
    )

    private var pending: VoteIntent? = null

    fun save(postId: String, side: Side) {
        pending = VoteIntent(postId = postId, side = side)
    }

    fun consume(): VoteIntent? {
        val value = pending
        pending = null
        return value
    }

    fun clear() {
        pending = null
    }
}