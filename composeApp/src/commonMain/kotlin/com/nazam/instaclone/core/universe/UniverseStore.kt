package com.nazam.instaclone.core.universe

/**
 * Store mémoire (KMP friendly).
 * On garde l'univers actif.
 */
object UniverseStore {

    private var current: Universe = Universe.FOOTBALL

    fun get(): Universe = current

    fun set(value: Universe) {
        current = value
    }

    fun resetToDefault() {
        current = Universe.FOOTBALL
    }
}
