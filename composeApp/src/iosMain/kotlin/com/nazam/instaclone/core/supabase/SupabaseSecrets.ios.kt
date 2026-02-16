package com.nazam.instaclone.core.supabase

import platform.Foundation.NSBundle

/**
 * iOS : on lit les secrets depuis Info.plist.
 * ✅ Important : ne pas mettre la vraie clé dans git si possible.
 */
internal actual object SupabaseSecrets {

    private const val KEY_URL = "SUPABASE_URL"
    private const val KEY_ANON = "SUPABASE_ANON_KEY"

    private fun readPlistString(key: String): String {
        val value = NSBundle.mainBundle.objectForInfoDictionaryKey(key) as? String
        require(!value.isNullOrBlank()) {
            "Missing or empty Info.plist key: $key"
        }
        return value
    }

    actual val url: String = readPlistString(KEY_URL)
    actual val anonKey: String = readPlistString(KEY_ANON)
}