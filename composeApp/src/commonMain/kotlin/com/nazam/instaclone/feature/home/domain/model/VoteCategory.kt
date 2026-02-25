package com.nazam.instaclone.feature.home.domain.model

import com.nazam.instaclone.core.ui.UiText
import instaclone.composeapp.generated.resources.Res
import instaclone.composeapp.generated.resources.category_cinema
import instaclone.composeapp.generated.resources.category_food
import instaclone.composeapp.generated.resources.category_football
import instaclone.composeapp.generated.resources.category_jeux
import instaclone.composeapp.generated.resources.category_musique
import instaclone.composeapp.generated.resources.category_none
import instaclone.composeapp.generated.resources.category_pays
import instaclone.composeapp.generated.resources.category_personnalite
import instaclone.composeapp.generated.resources.category_politique
import instaclone.composeapp.generated.resources.category_sport
import instaclone.composeapp.generated.resources.category_tech
import instaclone.composeapp.generated.resources.category_voyage

/**
 * Catégorie possible pour un vote.
 * - id : stable (DB)
 * - label : UiText (KMP + traduisible)
 */
data class VoteCategory(
    val id: String,
    val label: UiText
)

object VoteCategories {

    // ✅ id stable de notre univers principal
    const val FOOTBALL_ID: String = "football"

    val all: List<VoteCategory> = listOf(
        VoteCategory(id = FOOTBALL_ID, label = UiText.Resource(Res.string.category_football)),
        VoteCategory(id = "sport", label = UiText.Resource(Res.string.category_sport)),
        VoteCategory(id = "politique", label = UiText.Resource(Res.string.category_politique)),
        VoteCategory(id = "voyage", label = UiText.Resource(Res.string.category_voyage)),
        VoteCategory(id = "pays", label = UiText.Resource(Res.string.category_pays)),
        VoteCategory(id = "personnalite", label = UiText.Resource(Res.string.category_personnalite)),
        VoteCategory(id = "cinema", label = UiText.Resource(Res.string.category_cinema)),
        VoteCategory(id = "musique", label = UiText.Resource(Res.string.category_musique)),
        VoteCategory(id = "tech", label = UiText.Resource(Res.string.category_tech)),
        VoteCategory(id = "food", label = UiText.Resource(Res.string.category_food)),
        VoteCategory(id = "jeux", label = UiText.Resource(Res.string.category_jeux))
    )

    /**
     * Transforme un id en label.
     * Si inconnu ou vide -> "Sans catégorie"
     */
    fun labelFor(id: String): UiText {
        val safeId = id.trim()
        if (safeId.isBlank()) return UiText.Resource(Res.string.category_none)
        return all.firstOrNull { it.id == safeId }?.label ?: UiText.Resource(Res.string.category_none)
    }
}
