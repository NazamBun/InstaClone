package com.nazam.instaclone.core.universe

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Store mémoire (KMP friendly).
 * ✅ Par défaut : GLOBAL (on affiche tout)
 */
object UniverseStore {

    private val _universe = MutableStateFlow(Universe.GLOBAL)
    val universe: StateFlow<Universe> = _universe

    fun get(): Universe = _universe.value

    fun set(value: Universe) {
        _universe.value = value
    }

    fun resetToDefault() {
        _universe.value = Universe.GLOBAL
    }
}
