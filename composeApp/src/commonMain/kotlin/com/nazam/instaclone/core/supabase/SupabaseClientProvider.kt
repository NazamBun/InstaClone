package com.nazam.instaclone.core.supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {
    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SupabaseSecrets.url,
            supabaseKey = SupabaseSecrets.anonKey
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}