package com.nazam.instaclone.feature.home.presentation.ui.components.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun getUserLetter(displayName: String?, email: String?): String {
    val base = displayName?.trim().takeUnless { it.isNullOrBlank() }
        ?: email?.trim().takeUnless { it.isNullOrBlank() }
        ?: "?"
    return base.take(1).uppercase()
}

fun getLetterFromName(name: String?): String {
    val base = name?.trim().takeUnless { it.isNullOrBlank() } ?: "?"
    return base.take(1).uppercase()
}

fun formatCreatedAtHuman(createdAt: String): String {
    return try {
        val parts = createdAt.trim().split(" ")
        if (parts.size < 2) return createdAt

        val datePart = parts[0]
        val timePart = parts[1]
        val d = datePart.split("-")
        if (d.size != 3) return createdAt

        "${d[2]}/${d[1]}/${d[0]} • $timePart"
    } catch (_: Throwable) {
        createdAt
    }
}

@OptIn(ExperimentalTime::class)
fun formatTimeAgo(createdAtMillis: Long): String {
    if (createdAtMillis <= 0L) return "Publié récemment"

    val now = Clock.System.now().toEpochMilliseconds()
    val diff = (now - createdAtMillis).coerceAtLeast(0L)

    val minute = 60_000L
    val hour = 60 * minute
    val day = 24 * hour
    val month = 30 * day
    val year = 365 * day

    return when {
        diff < minute -> "Publié à l'instant"
        diff < hour -> {
            val value = diff / minute
            "Publié il y a $value " + if (value == 1L) "minute" else "minutes"
        }
        diff < day -> {
            val value = diff / hour
            "Publié il y a $value " + if (value == 1L) "heure" else "heures"
        }
        diff < month -> {
            val value = diff / day
            "Publié il y a $value " + if (value == 1L) "jour" else "jours"
        }
        diff < year -> {
            val value = diff / month
            "Publié il y a $value mois"
        }
        else -> {
            val value = diff / year
            "Publié il y a $value " + if (value == 1L) "an" else "ans"
        }
    }
}
