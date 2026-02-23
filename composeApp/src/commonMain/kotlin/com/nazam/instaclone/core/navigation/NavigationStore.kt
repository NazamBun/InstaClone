package com.nazam.instaclone.core.navigation

/**
 * Stockage en mémoire (KMP friendly).
 *
 * - afterLogin : où aller après connexion
 * - authReturn : où revenir si l’utilisateur ferme Login (flèche retour)
 */
object NavigationStore {

    private var afterLogin: Screen? = null
    private var authReturn: Screen? = null

    fun setAfterLogin(screen: Screen) {
        afterLogin = screen
    }

    /**
     * Écran à afficher si l’utilisateur veut "revenir" depuis Login.
     * Exemple :
     * Home -> clique Create (protégé) -> Login
     * Flèche retour -> revient sur Home
     */
    fun setAuthReturn(screen: Screen) {
        authReturn = screen
    }

    fun consumeAfterLogin(): Screen? {
        val target = afterLogin
        afterLogin = null
        return target
    }

    /**
     * Important :
     * - si l’utilisateur quitte Login, on annule aussi "afterLogin"
     *   (sinon ça peut rediriger plus tard alors qu’il a annulé)
     */
    fun consumeAuthReturn(): Screen? {
        val target = authReturn
        authReturn = null
        afterLogin = null
        return target
    }

    fun clear() {
        afterLogin = null
        authReturn = null
    }
}
