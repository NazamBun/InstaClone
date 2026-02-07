package com.nazam.instaclone.core.supabase

import platform.Foundation.NSBundle

internal actual object SupabaseSecrets {

    private fun read(key: String): String {
        val value = NSBundle.mainBundle.objectForInfoDictionaryKey(key) as? String
        require(!value.isNullOrBlank()) { "Missing Info.plist key: $key" }
        return value
    }

    actual val url: String = read("SUPABASE_URL")
    actual val anonKey: String = read("SUPABASE_ANON_KEY")
}