package com.nazam.instaclone.core.universe

/**
 * "Univers" = thème principal de l'app.
 * V1 : FOOTBALL (par défaut) + GLOBAL (tout).
 *
 * ✅ KMP friendly
 * ✅ Id stable (si plus tard tu stockes ça en DB)
 */
enum class Universe(val id: String) {
    FOOTBALL("football"),
    GLOBAL("global")
}
