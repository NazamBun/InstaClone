package com.nazam.instaclone.feature.home.presentation.categories

/**
 * Indique pourquoi on ouvre l'écran Categories :
 * - CREATE_POST : choisir la catégorie du post
 * - HOME_FILTER : choisir un filtre pour le feed Home
 */
object CategorySelectionStore {

    enum class Target {
        CREATE_POST,
        HOME_FILTER
    }

    private var target: Target = Target.CREATE_POST

    fun setTarget(value: Target) {
        target = value
    }

    fun getTarget(): Target = target
}