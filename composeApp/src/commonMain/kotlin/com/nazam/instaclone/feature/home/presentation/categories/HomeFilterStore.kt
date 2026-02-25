package com.nazam.instaclone.feature.home.presentation.categories

import com.nazam.instaclone.feature.home.domain.model.VoteCategories

/**
 * Stocke le filtre de catégorie choisi pour l'écran Home.
 * KMP friendly : mémoire uniquement.
 *
 * ✅ Par défaut : Football (notre univers principal)
 */
object HomeFilterStore {

    private var selectedCategoryId: String = VoteCategories.FOOTBALL_ID

    fun setCategory(id: String) {
        selectedCategoryId = id
    }

    fun getCategory(): String = selectedCategoryId

    fun clear() {
        selectedCategoryId = VoteCategories.FOOTBALL_ID
    }
}
