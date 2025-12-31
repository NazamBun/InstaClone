package com.nazam.instaclone.core.navigation

/**
 * Petit stockage en mémoire (KMP friendly).
 * But : retenir où on doit aller après un login.
 *
 * Exemple :
 * - CreatePost -> pas connecté -> on met afterLogin = CreatePost -> on va Login
 * - Login OK -> on consomme afterLogin -> on navigue vers CreatePost
 */
object NavigationStore {

    private var afterLogin: Screen? = null

    fun setAfterLogin(screen: Screen) {
        afterLogin = screen
    }

    /**
     * Important : "consume" = on lit puis on efface.
     * Comme ça, ça ne boucle pas.
     */
    fun consumeAfterLogin(): Screen? {
        val target = afterLogin
        afterLogin = null
        return target
    }

    fun clear() {
        afterLogin = null
    }
}