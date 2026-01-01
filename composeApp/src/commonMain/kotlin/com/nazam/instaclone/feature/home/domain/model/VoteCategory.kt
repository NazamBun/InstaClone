package com.nazam.instaclone.feature.home.domain.model

/**
 * Catégories possibles pour un vote.
 * On garde un "id" stable (pour la DB) + un "label" (pour l'affichage).
 */
data class VoteCategory(
    val id: String,
    val label: String
)

object VoteCategories {

    val all: List<VoteCategory> = listOf(
        VoteCategory(id = "sport", label = "Sport"),
        VoteCategory(id = "politique", label = "Politique"),
        VoteCategory(id = "voyage", label = "Voyage"),
        VoteCategory(id = "pays", label = "Pays"),
        VoteCategory(id = "personnalite", label = "Personnalité"),
        VoteCategory(id = "cinema", label = "Cinéma"),
        VoteCategory(id = "musique", label = "Musique"),
        VoteCategory(id = "tech", label = "Tech"),
        VoteCategory(id = "food", label = "Food"),
        VoteCategory(id = "jeux", label = "Jeux")
    )

    /**
     * Transforme un id (ex: "sport") en label (ex: "Sport").
     * Si l'id est inconnu ou vide, on affiche un texte par défaut.
     */
    fun labelFor(id: String): String {
        val safeId = id.trim()
        if (safeId.isBlank()) return "Sans catégorie"
        return all.firstOrNull { it.id == safeId }?.label ?: "Sans catégorie"
    }
}
