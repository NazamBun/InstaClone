package com.nazam.instaclone.feature.home.presentation.categories

/**
 * Filtre catégorie (dans l'univers).
 * V1 : simple mémoire.
 *
 * - "" = pas de filtre (tout)
 */
object HomeFilterStore {

    private var selectedCategoryId: String = ""

    fun setCategory(id: String) {
        selectedCategoryId = id
    }

    fun setAll() {
        selectedCategoryId = ""
    }

    fun getCategory(): String = selectedCategoryId
}
