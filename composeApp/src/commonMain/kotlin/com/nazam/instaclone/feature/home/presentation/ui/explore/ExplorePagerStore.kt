package com.nazam.instaclone.feature.home.presentation.ui.explore

/**
 * Petit store en mémoire (KMP friendly).
 * Sert à passer des infos de Explore -> ExplorePager
 * sans dépendre d’un framework de navigation.
 */
object ExplorePagerStore {

    private var categoryId: String = ""
    private var startPostId: String = ""

    fun open(categoryId: String, startPostId: String) {
        this.categoryId = categoryId
        this.startPostId = startPostId
    }

    fun getCategoryId(): String = categoryId
    fun getStartPostId(): String = startPostId

    fun clear() {
        categoryId = ""
        startPostId = ""
    }
}