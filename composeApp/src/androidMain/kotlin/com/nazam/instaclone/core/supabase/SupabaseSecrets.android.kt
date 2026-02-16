package com.nazam.instaclone.core.supabase

import com.nazam.instaclone.BuildConfig

internal actual object SupabaseSecrets {
    actual val url: String = BuildConfig.SUPABASE_URL
    actual val anonKey: String = BuildConfig.SUPABASE_ANON_KEY
}