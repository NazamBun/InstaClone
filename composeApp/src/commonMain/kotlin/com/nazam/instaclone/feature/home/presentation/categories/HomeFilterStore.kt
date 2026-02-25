package com.nazam.instaclone.feature.home.presentation.categories

import com.nazam.instaclone.feature.home.domain.model.VoteCategories

/**
 * Filtre de catégorie pour Home / Explore.
 *
 * Règle :
 * - clear() = revient à l'univers par défaut (Football)
 * - setAll() = affiche tout (id vide)
 *
 * KMP friendly : mémoire seulement.
 */
object HomeFilterStore {

    // ✅ Par défaut : univers Football
    private var selectedCategoryId: String = VoteCategories.FOOTBALL_ID

    fun setCategory(id: String) {
        selectedCategoryId = id.trim()
    }

    /**
     * ✅ "Tout" : pas de filtre.
     * On met vide, et l'UI montrera tous les posts.
     */
    fun setAll() {
        selectedCategoryId = ""
    }

    fun getCategory(): String = selectedCategoryId

    /**
     * ✅ Retour à l'univers par défaut (Football).
     * Utilisé quand on veut "revenir à l'accueil foot".
     */
    fun clear() {
        selectedCategoryId = VoteCategories.FOOTBALL_ID
    }
}
