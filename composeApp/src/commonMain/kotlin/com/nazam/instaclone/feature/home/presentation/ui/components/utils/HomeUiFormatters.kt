package com.nazam.instaclone.feature.home.presentation.ui.components.utils

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

        val datePart = parts[0] // yyyy-MM-dd
        val timePart = parts[1] // HH:mm

        val d = datePart.split("-")
        if (d.size != 3) return createdAt

        val yyyy = d[0]
        val mm = d[1]
        val dd = d[2]

        "$dd/$mm/$yyyy • $timePart"
    } catch (_: Throwable) {
        createdAt
    }
}