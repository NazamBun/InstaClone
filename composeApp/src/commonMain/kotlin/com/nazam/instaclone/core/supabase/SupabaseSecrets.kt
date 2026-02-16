package com.nazam.instaclone.core.supabase

/**
 * Common = pas de secrets.
 * Android/iOS donnent les vraies valeurs.
 */
internal expect object SupabaseSecrets {
    val url: String
    val anonKey: String
}