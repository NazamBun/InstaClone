package com.nazam.instaclone.feature.home.presentation.categories

/**
 * Stocke le filtre de catégorie choisi pour l'écran Home.
 * KMP friendly : mémoire uniquement.
 */
object HomeFilterStore {

    private var selectedCategoryId: String = ""

    fun setCategory(id: String) {
        selectedCategoryId = id
    }

    fun getCategory(): String = selectedCategoryId

    fun clear() {
        selectedCategoryId = ""
    }
}