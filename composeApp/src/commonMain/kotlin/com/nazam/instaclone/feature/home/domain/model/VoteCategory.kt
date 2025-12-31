package com.nazam.instaclone.feature.home.domain.model

/**
 * Catégories possibles pour un vote.
 * On garde un "id" stable + un "label" pour afficher.
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
}